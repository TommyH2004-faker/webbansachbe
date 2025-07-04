package com.example.web_bookstore_be.service.cart;

import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface CartService {
    public ResponseEntity<?> save(JsonNode jsonNode);
    public ResponseEntity<?> update(JsonNode jsonNode);
    //getAll
    public ResponseEntity<?> getAll();
}
