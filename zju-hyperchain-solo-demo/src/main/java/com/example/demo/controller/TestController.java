package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController {

    @RequestMapping("/")
    public String Test(){
        return "login";
    }
    @RequestMapping("/user_pro")
    public  String  pro(){
        return "profile";
    }
    @RequestMapping("/test")
    public String test(Model model){
        model.addAttribute("msg", "Hello World!");
        return "test";
    }
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    @RequestMapping("/chooseTemplate")
    public String chooseTemplate(){
        return "chooseTemplate";
    }
    @RequestMapping("/updateTemplate")
    public String updateTemplate(){
        return "updateTemplate";
    }
    @RequestMapping("/addTemplate")
    public String addTemplate(){
        return "addTemplate";
    }
    @RequestMapping("/contract")
    public String contract(){
        return "contract";
    }
    @RequestMapping("/createContract")
    public  String createContract(){
        return "createContract";
    }
    @RequestMapping("/enValidify.html")
    public String goEnvalidfy(){
        return "enValidify";
    }
    @RequestMapping("/showContractHistory.html")
    public String goShowContractHistory(){
        return "showContractHistory";
    }
}
