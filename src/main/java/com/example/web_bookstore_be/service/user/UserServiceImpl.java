package com.example.web_bookstore_be.service.user;

import com.example.web_bookstore_be.dao.NguoiDungRepository;
import com.example.web_bookstore_be.dao.QuyenRepository;
import com.example.web_bookstore_be.entity.Notification;
import com.example.web_bookstore_be.entity.Role;
import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.security.JwtRespone;
import com.example.web_bookstore_be.service.JWT.JwtService;
import com.example.web_bookstore_be.service.UploadImage.UploadImageService;
import com.example.web_bookstore_be.service.email.EmailSerVice;
import com.example.web_bookstore_be.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserSerVice {
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private QuyenRepository quyenRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailSerVice emailSerVice;
    @Autowired
    private UploadImageService uploadImageService;
    @Autowired
    private JwtService jwtService;
    private final ObjectMapper objectMapper;
    public UserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }


    @Override
    public ResponseEntity<?> register(User user) {
        if(nguoiDungRepository.findByUsername(user.getUsername()) != null){
            return ResponseEntity.badRequest().body("Tai khoan da ton tai!");
        }
        if(nguoiDungRepository.findByEmail(user.getEmail()) != null){
            return ResponseEntity.badRequest().body("Email da ton tai!");
        }

        String encodedPassword =  passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setAvatar("");


        user.setEnabled(false);
        List<Role> roles = new ArrayList<>();
        roles.add(quyenRepository.findBynameRole("CUSTOMER"));
        user.setListRoles(roles);
        user.setActivationCode(taomaTokenKichHoat());
        // luu user vao database
        User user_daDangKy = nguoiDungRepository.save(user);
        // gui email cho nguoi dung de kich hoat
        guiEmailKichHoat(user.getEmail(), user.getActivationCode());

        return ResponseEntity.ok("Dang ky thanh cong!");
    }
    private String taomaTokenKichHoat() {
        return UUID.randomUUID().toString();
    }

    private void guiEmailKichHoat(String email, String maKichHoat) {
        String subject = "Kích hoạt tài khoản của bạn tại WebBanSach";
        String text = "Vui lòng sử dụng mã sau để kich hoạt cho tài khoản <" + email + ">:<html><body><br/><h1>" + maKichHoat + "</h1></body></html>";
        text += "<br/> Click vào đường link để kích hoạt tài khoản: ";
        String encodedEmail = URLEncoder.encode(email);
        String url = "http://localhost:3000/active-account/"+email+"/"+maKichHoat;
        text+=("<br/> <a href="+url+">"+url+"</a> ");
        emailSerVice.sendEmail("hiept81331@gmail.com", email, subject, text);

    }

    public ResponseEntity<?> kichHoatTaiKhoan(String email, String maKichHoat) {// Giải mã email
        User user = nguoiDungRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(new Notification("Nguoi dung khong ton tai !"));
        }
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(new Notification("Tài khoản đã được kích hoạt!"));
        }
        if (maKichHoat.equals(user.getActivationCode())) {
            user.setEnabled(true);
            nguoiDungRepository.save(user);
            return ResponseEntity.ok("Kích hoạt tài khoản thành công!");
        }else{
            return ResponseEntity.badRequest().body(new Notification("Mã kích hoạt không đúng!"));
        }

    }
    @Override
    public ResponseEntity<?> save(JsonNode userJson, String option) {
        try{
            User user = objectMapper.treeToValue(userJson, User.class);

            // Kiểm tra username đã tồn tại chưa
            if (!option.equals("update")) {
                if (nguoiDungRepository.existsByUsername(user.getUsername())) {
                    return ResponseEntity.badRequest().body(new Notification("Username đã tồn tại."));
                }

                // Kiểm tra email
                if (nguoiDungRepository.existsByEmail(user.getEmail())) {
                    return ResponseEntity.badRequest().body(new Notification("Email đã tồn tại."));
                }
            }

            // Set ngày sinh cho user
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse((String.valueOf(userJson.get("dateOfBirth")))) );
            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());
            user.setDateOfBirth(dateOfBirth);

            // Set role cho user
            int idRoleRequest = Integer.parseInt(String.valueOf(userJson.get("role")));
            Optional<Role> role = quyenRepository.findById(idRoleRequest);
            List<Role> roles = new ArrayList<>();
            roles.add(role.get());
            user.setListRoles(roles);

            // Mã hoá mật khẩu
            if (!(user.getPassword() == null)) { // Trường hợp là thêm hoặc thay đổi password
                String encodePassword =passwordEncoder.encode(user.getPassword());
                user.setPassword(encodePassword);
            } else {
                // Trường hợp cho update không thay đổi password
                Optional<User> userTemp = nguoiDungRepository.findById(user.getIdUser());
                user.setPassword(userTemp.get().getPassword());
            }

            // Set avatar
            String avatar = (formatStringByJson(String.valueOf((userJson.get("avatar")))));
            if (avatar.length() > 500) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(avatar);
                String avatarURL = uploadImageService.uploadImage(avatarFile, "User_" + user.getIdUser());
                user.setAvatar(avatarURL);
            }

            nguoiDungRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> delete(int id) {
        try{
            Optional<User> user = nguoiDungRepository.findById(id);

            if (user.isPresent()) {
                String imageUrl = user.get().getAvatar();

                if (imageUrl != null) {
                    uploadImageService.deleteImage(imageUrl);
                }

                nguoiDungRepository.deleteById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> changePassword(JsonNode userJson) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(userJson.get("idUser"))));
            String newPassword = formatStringByJson(String.valueOf(userJson.get("newPassword")));
            System.out.println(idUser);
            System.out.println(newPassword);
            Optional<User> user = nguoiDungRepository.findById(idUser);
            user.get().setPassword(passwordEncoder.encode(newPassword));
            nguoiDungRepository.save(user.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> changeAvatar(JsonNode userJson) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(userJson.get("idUser"))));
            String dataAvatar = formatStringByJson(String.valueOf(userJson.get("avatar")));

            Optional<User> user = nguoiDungRepository.findById(idUser);

            // Xoá đi ảnh trước đó trong cloudinary
            if (user.get().getAvatar().length() > 0) {
                uploadImageService.deleteImage(user.get().getAvatar());
            }

            if (Base64ToMultipartFileConverter.isBase64(dataAvatar)) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(dataAvatar);
                String avatarUrl = uploadImageService.uploadImage(avatarFile, "User_" + idUser);
                user.get().setAvatar(avatarUrl);
            }

            User newUser =  nguoiDungRepository.save(user.get());
            final String jwtToken = jwtService.generateToken(newUser.getUsername());
            return ResponseEntity.ok(new JwtRespone(jwtToken));

        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<?> updateProfile(JsonNode userJson) {
        try{
            User userRequest = objectMapper.treeToValue(userJson, User.class);
            Optional<User> user = nguoiDungRepository.findById(userRequest.getIdUser());

            user.get().setFirstName(userRequest.getFirstName());
            user.get().setLastName(userRequest.getLastName());
            // Format lại ngày sinh
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(userJson.get("dateOfBirth")))));
            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());

            user.get().setDateOfBirth(dateOfBirth);
            user.get().setPhoneNumber(userRequest.getPhoneNumber());
            user.get().setDeliveryAddress(userRequest.getDeliveryAddress());
            user.get().setGender(userRequest.getGender());

            nguoiDungRepository.save(user.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> forgotPassword(JsonNode jsonNode) {
        try{
            User user = nguoiDungRepository.findByEmail(formatStringByJson(jsonNode.get("email").toString()));

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Đổi mật khẩu cho user
            String passwordTemp = taomaTokenKichHoat();
            user.setPassword(passwordEncoder.encode(passwordTemp));
            nguoiDungRepository.save(user);

            // Gửi email đê nhận mật khẩu
            sendEmailForgotPassword(user.getEmail(), passwordTemp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private void sendEmailForgotPassword(String email, String password) {
        String subject = "Reset mật khẩu";
        String message = "Mật khẩu tạm thời của bạn là: <strong>" + password + "</strong>";
        message += "<br/> <span>Vui lòng đăng nhập và đổi lại mật khẩu của bạn</span>";
        try {
            emailSerVice.sendEmail("hiept81331@gmail.com", email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = nguoiDungRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Tai khoan khong ton tai!");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                rolesToAuthorities(user.getListRoles())
        );
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> quyens) {
        return quyens.stream()
                .map(quyen -> new SimpleGrantedAuthority(quyen.getNameRole()))
                .collect(Collectors.toList());
    }
    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}