package com.example.demo.controller;

import com.example.demo.entity.ContractTemplate;
import com.example.demo.repository.contractTemplateRepository;
import com.example.demo.repository.enterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ContractTemplateController {
    /*
    * 添加模板，返回模板修改界面
    *
    * */
    @Autowired
    private contractTemplateRepository ctrepository;
    @PostMapping("/addTemplate")
    public String addTemplate(ContractTemplate template, HttpSession session){
        System.out.println(template.toString());
        Object username = session.getAttribute("username");
        template.setAccountId((String) username);//将认证信息存入数据库
        ctrepository.save(template);
        return "redirect:/updateTemplatePage";
    }

    /*
    * 返回模板添加界面
    * */
    @GetMapping("/addTemplatePage")
    public String goAddTemplatePage(){
        return "addTemplate";
    }
    /*
    * 返回模板修改界面
    * 查询所有模板
    * */
    @GetMapping("/updateTemplatePage")
    public String goUpdateTemplatepage(Model model, HttpSession session){
        String username = (String) session.getAttribute("username");
        List<ContractTemplate> lists = ctrepository.findAllByAccountId(username);
        model.addAttribute("lists", lists);
        return "updateTemplate";
    }
    /*
    * 修改模板内容，并返回模板列表界面
    * */
    @PostMapping("/updateTemplate")
    public String updateTemplate(ContractTemplate contractTemplate){
//        System.out.println(contractTemplate);
        ContractTemplate ct1 = ctrepository.findBytemplateId(contractTemplate.getTemplateId());
        ct1.setTemplateName(contractTemplate.getTemplateName());
        ct1.setTemplateContext(contractTemplate.getTemplateContext());
        ctrepository.deleteContractTemplateByTemplateId(contractTemplate.getTemplateId());
        ctrepository.save(ct1);
        return "redirect:/updateTemplatePage";
    }
    /*
    *  删除模板，并返回模板列表界面
    * */
    @PostMapping("/delete")
    public String deleteTemplate(@RequestParam("id") int id){
//        System.out.println(id);
        ctrepository.deleteContractTemplateByTemplateId(id);
        return "redirect:/updateTemplatePage";
    }
}
