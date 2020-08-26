package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ValidifyController {
    @PostMapping("/indValidity")
    public String indValidify(@RequestParam("name") String name, @RequestParam("idCard") String idCard, Model model, HttpSession session){
//        System.out.println("name = "+name);
//        System.out.println("idCard = "+idCard);
        Object username = session.getAttribute("username");
        System.out.println("username = " + username);
        return "/profile";
    }

    @GetMapping("/profilePage")
    public String goRegisterPage(String name,String idCard){
        return "profile";
    }
}
