
package com.example.web_bookstore_be.service;

import com.example.web_bookstore_be.dao.NguoiDungRepository;
import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.service.user.UserServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testChangePassword_WithFakeDatabase() throws Exception {

        User user = new User();
        user.setEmail("changepw@test.com");
        user.setPassword("123456");
        user = nguoiDungRepository.save(user);


        String json = """
            {
              "idUser": %d,
              "newPassword": "newPassword123"
            }
        """.formatted(user.getIdUser());

        JsonNode node = objectMapper.readTree(json);


        ResponseEntity<?> response = userService.changePassword(node);


        assertEquals(200, response.getStatusCodeValue());

        User updatedUser = nguoiDungRepository.findById(user.getIdUser()).orElseThrow();
        assertNotEquals("123456", updatedUser.getPassword());  // Mật khẩu đã bị mã hóa
    }
    @Test
    public void testUpdateProfile_WithFakeDatabase() throws Exception {
        User user = new User();
        user.setEmail("updateprofile@test.com");
        user = nguoiDungRepository.save(user);

        String json = """
        {
          "idUser": %d,
          "firstName": "John",
          "lastName": "Doe",
          "phoneNumber": "0987654321",
          "deliveryAddress": "123 Street",
          "gender": "M",
          "dateOfBirth": "2000-01-01T00:00:00.000Z"
        }
    """.formatted(user.getIdUser());

        JsonNode node = objectMapper.readTree(json);


        ResponseEntity<?> response = userService.updateProfile(node);

        assertEquals(200, response.getStatusCodeValue());

        User updated = nguoiDungRepository.findById(user.getIdUser()).orElseThrow();
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
    }


}

/*package com.example.web_bookstore_be.service;

import com.example.web_bookstore_be.dao.NguoiDungRepository;
import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.service.user.UserServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @Rollback(false)
    public void testChangePassword_WithFakeDatabase() throws Exception {

        User user = new User();
        user.setEmail("changepw" + System.currentTimeMillis() + "@test.com");
        String rawPassword = "123456";
        user.setPassword(passwordEncoder.encode(rawPassword));
        user = nguoiDungRepository.save(user);


        String json = """
            {
              "idUser": %d,
              "newPassword": "newPassword123"
            }
        """.formatted(user.getIdUser());

        JsonNode node = objectMapper.readTree(json);
        ResponseEntity<?> response = userService.changePassword(node);

        assertEquals(200, response.getStatusCodeValue());


        User updatedUser = nguoiDungRepository.findById(user.getIdUser()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPassword()));
        System.out.println("Updated encoded password: " + updatedUser.getPassword());
    }

    @Test
    @Rollback(false)
    public void testUpProfile_WithFakeDatabase() throws Exception {

        User user = new User();
        user.setEmail("updateprofile" + System.currentTimeMillis() + "@test.com");
        user.setPassword(passwordEncoder.encode("abc123"));

        user = nguoiDungRepository.save(user);


        String json = """
            {
              "idUser": %d,
              "firstName": "John",
              "lastName": "Doe",
              "phoneNumber": "0987654321",
              "deliveryAddress": "123 Street",
              "gender": "M",
              "dateOfBirth": "2000-01-01T00:00:00.000Z"
            }
        """.formatted(user.getIdUser());

        JsonNode node = objectMapper.readTree(json);
        ResponseEntity<?> response = userService.updateProfile(node);

        assertEquals(200, response.getStatusCodeValue());


        User updated = nguoiDungRepository.findById(user.getIdUser()).orElseThrow();
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertTrue(passwordEncoder.matches("abc123", updated.getPassword())); // Mật khẩu giữ nguyên
        System.out.println("Updated user: " + updated.getFirstName() + " " + updated.getLastName());
    }
}*/

