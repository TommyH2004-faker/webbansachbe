package com.example.web_bookstore_be.rest;

import com.example.web_bookstore_be.dao.ChiTietDonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    private ChiTietDonHangRepository chiTietDonHangRepository;
@Autowired
    public Test(ChiTietDonHangRepository chiTietDonHangRepository) {
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }
    @GetMapping("/test")
    public String test() {
        return "Hello";
    }
}
