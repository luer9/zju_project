package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.repository.usersRepository;
import org.apache.bcel.generic.ARETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    /*
    用户进行登录,登录成功进入主界面
    登录失败提示错误信息
    */
    @Autowired
    private usersRepository repository;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("optionsRadiosInline") String option, Model model, RedirectAttributes redirectAttributes, HttpSession session){
//        System.out.println("username = "+username);
//        System.out.println("password = "+ password);
        String aid = username;
        Users needAcquired = repository.findByAccountId(aid);
        if(needAcquired == null){
            //TODO:显示账户不存在
            redirectAttributes.addFlashAttribute("msg", "账户不存在");
        }else{
            String pwd = needAcquired.getPwd();
            if(pwd.equals(password)){
                boolean op = option.equals("2");
                boolean real = needAcquired.accountType;
                if(real == op){
                //TODO:登录成功，跳转页面
                //TODO:重定向到主页，将用户名传入session
                session.setAttribute("username", username);

                return "redirect:/index";
                }else{
                    redirectAttributes.addFlashAttribute("msg", "确认账户类型");
                }
            }else{
                //TODO:显示密码错误
                redirectAttributes.addFlashAttribute("msg", "密码错误");
            }
        }
        return "redirect:/loginPage";
    }
    /*
    * 注册账户，返回登录界面
    * */
    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password,
                           @RequestParam("phoneNumber") String phoneNumber,@RequestParam("optionsRadiosInline") String option,
                           RedirectAttributes redirectAttributes){
        String aid = username;
        Users needAcquired = repository.findByAccountId(aid);
        if(needAcquired != null){
            System.out.println("用户名已被使用");
            //TODO：显示用户名已被使用
            redirectAttributes.addFlashAttribute("msg", "用户名已存在");
            return "redirect:/registerPage";
        }
        else{
            if(option.equals("option2")){
                repository.save(new Users(username,password,phoneNumber,true));//企业账户
//                    System.out.println("企业账户注册成功");
            }else{
                repository.save(new Users(username,password,phoneNumber,false));//个人账户
//                    System.out.println("个人账户注册成功");
            }
            //TODO:显示注册成功，跳转登录界面
            return "redirect:/loginPage";
        }
        //TODO:返回注册界面
    }
    /*
    * 忘记密码处理，返回登录界面
    * */
    @PostMapping("/forgetPwd")
    public String forgetPwd(){
        return "redirect:/loginPage";
    }
    @GetMapping("/signout")
    public String signout(HttpSession session){
        session.removeAttribute("username");
        return "redirect:/";
    }
    @GetMapping("/loginPage")
    public String goLoginPage(){
        return "login";
    }
    @GetMapping("/registerPage")
    public String goRegisterPage(String username,String password){
        return "register";
    }
    @GetMapping("/forgetPwdPage")
    public String goForgetPwdPage(){
        return "forgetPwd";
    }
}
