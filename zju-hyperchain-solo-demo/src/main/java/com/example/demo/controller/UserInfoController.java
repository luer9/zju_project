package com.example.demo.controller;

import cn.hyperchain.sdk.account.Account;
import cn.hyperchain.sdk.common.utils.ByteUtil;
import cn.hyperchain.sdk.crypto.ecdsa.ECKey;
import cn.hyperchain.sdk.exception.RequestException;
import com.example.demo.entity.Enterprise;
import com.example.demo.entity.Individual;
import com.example.demo.entity.Users;
import com.example.demo.repository.enterpriseRepository;
import com.example.demo.repository.individualRepository;
import com.example.demo.repository.usersRepository;
import com.example.demo.service.PersonService;
import com.example.demo.service.serviceImpl.PersonServiceImpl;
import com.google.api.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;

@Controller
public class UserInfoController {
    Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    public static PersonService personService = new PersonServiceImpl();

    @Autowired
    private individualRepository indrepository;
    @Autowired
    private usersRepository usersRepository;
    @Autowired
    private enterpriseRepository entrepository;
    /*
    * 查询是否认证，若已认证 msg为1，否则为空
    *
    * 返回个人认证界面
    * */
    @GetMapping("/personCertificationPage")
    public String goPersonCertification(HttpSession session, Model model) throws IOException, RequestException {
        String username = (String)session.getAttribute("username");
        Individual individual = indrepository.findByAccountId(username);
       // System.out.println(individual);

        if(individual!=null && personService.queryPersonInfo(individual.getIdCard()) != null){
            model.addAttribute("msg", "1");
        }


        return "indValidify";
    }
    /*
     * 查询是否认证，若已认证 msg为1，否则为空
     *
     * 返回企业认证界面
     * */
    @GetMapping("/enterpriseCertificationPage")
    public String goEnterpriseCertificationPage(HttpSession session, Model model) throws RequestException {
        String username = (String)session.getAttribute("username");
        Enterprise enterprise = entrepository.findEnterpriseByAccountId(username);
        if(enterprise == null) return "enValidify";
        Enterprise enterprise1 = personService.queryEnterpriseInfo(enterprise.getRegisNumber());
        if(enterprise1 != null) model.addAttribute("msg", "1");
        //model.addAttribute("msg", "1");
        return "enValidify";
    }
    /*
    * 进行个人认证，将个人信息上链
    * 并返回主界面
    * */


    @PostMapping("/personCertification")
    public String personCertification(Individual individual,HttpSession session) throws IOException, RequestException {
//        logger.info(individual.getName() + " " + individual.getIdCard());
        Object username = session.getAttribute("username");
        individual.setAccountId((String) username);//将认证信息存入数据库
        indrepository.save(individual);
        personService.addPersonInfo(individual); //将个人信息上链
        return "redirect:/index";
    }
    /*
    * 进行企业认证，将企业信息上链
    * 并返回主界面
    * */



    @PostMapping("/enterpriseCertification")
    public String enterpriseCertification(Enterprise enterprise,HttpSession session) throws RequestException {
        //logger.info(enterprise.getLocation());
        //System.out.println(enterprise.toString());
        Object username = session.getAttribute("username");
        enterprise.setAccountId((String) username);//将认证信息存入数据库
        entrepository.save(enterprise);
        personService.addEnterpriseInfo(enterprise);//将企业认证信息上链
        return "redirect:/index";
    }
    @GetMapping("/userInfo")
    public String userInfo(HttpSession session, Model model) throws IOException, RequestException {
        String username = (String) session.getAttribute("username");
        Individual individual = indrepository.findByAccountId(username);
        if(individual != null){
            Individual individual1 = personService.queryPersonInfo(individual.getIdCard());
            if(individual1 != null) model.addAttribute("person", individual1);
        }
        Enterprise enterprise = entrepository.findEnterpriseByAccountId(username);
        System.out.println(enterprise);
        if(enterprise != null) {
            Enterprise enterprise1 = personService.queryEnterpriseInfo(enterprise.getRegisNumber());
            if(enterprise1 != null) model.addAttribute("enterprise", enterprise1);
        }
        return "profile";
    }
    @GetMapping("/passwordPage")
    public String goPasswordPage(){
        return "passwordPage";
    }
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("usedPassword") String usedPassword, @RequestParam("newPassword") String newPassword, HttpSession session, RedirectAttributes redirectAttributes){
        String username = (String) session.getAttribute("username");
        Users users = usersRepository.findByAccountId(username);
        if(users.getPwd().equals(usedPassword)){
            users.setPwd(newPassword);
            usersRepository.save(users);
            redirectAttributes.addFlashAttribute("msg", "密码修改成功");
        }
        else redirectAttributes.addFlashAttribute("msg", "密码修改失败");
        return "redirect:/passwordPage";
    }
}
