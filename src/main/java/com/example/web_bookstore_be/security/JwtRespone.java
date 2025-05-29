package com.example.web_bookstore_be.security;

public class JwtRespone {
    private final  String jwt ;

    public JwtRespone(String jwt){
        this.jwt=jwt;
    }
    public String getJwt(){
        return jwt;
    }

}
