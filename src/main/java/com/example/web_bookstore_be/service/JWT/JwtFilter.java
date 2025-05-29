package com.example.web_bookstore_be.service.JWT;

import com.example.web_bookstore_be.service.user.UserSerVice;
import com.example.web_bookstore_be.service.util.UserSecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserSecurityService userDetailSerVice;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // lay header Authorization
        String token = null;  // khai bao token
        String username = null; // khai bao username
        if (authHeader != null && authHeader.startsWith("Bearer ")) {  // kiem tra xem co token trong header khong
            token = authHeader.substring(7); // cat chuoi tu vi tri thu 7
            username = jwtService.extractUsername(token); // lay username tu token
        }
        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){ //
            UserDetails userDetails = userDetailSerVice.loadUserByUsername(username); //
            if(jwtService.validateToken(token,userDetails)){  // kiem tra token co hop le khong
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                // set thong tin nguoi dung
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // set thong tin nguoi dung
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response); // chuyen tiep request
    }
}