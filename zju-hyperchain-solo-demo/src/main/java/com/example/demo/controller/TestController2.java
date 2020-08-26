package com.example.demo.controller;

import com.example.demo.entity.Users;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController2 {
    @GetMapping("/test")
    public Users test() {
        Users users = new Users();
        users.setAccountId("9090");
        return users;
    }
}
