package com.example.demo.controller;

import cn.hyperchain.sdk.exception.RequestException;
import com.example.demo.entity.*;
import com.example.demo.repository.contractRepository;
import com.example.demo.repository.contractTemplateRepository;
import com.example.demo.repository.individualRepository;
import com.example.demo.repository.usersRepository;
import com.example.demo.service.ContractService;
import com.example.demo.service.PersonService;
import com.example.demo.service.serviceImpl.ContractServiceImpl;
import com.example.demo.service.serviceImpl.PersonServiceImpl;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class ContractController {

    @Autowired
    private contractTemplateRepository ctrepository;
    @Autowired
    private contractRepository contractRepository;
    @Autowired
    private usersRepository usersRepository;
    @Autowired
    private individualRepository individualRepository;
    private static  ContractService contractService = new ContractServiceImpl();
    private static int id = 1;
    /*
    * 创建合同，将合同信息上链
    * 并返回合同列表界面
    * */
    @PostMapping("/createContract")
    public String createContract(Contract contract, HttpSession session) throws RequestException, IOException {
        String username = (String) session.getAttribute("username");
        contract.setAccountId(username);
        contract.setState(0);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = formatter.format(calendar.getTime()).toString();

        contract.setCreateDate(time);
        contract.setContractId(id++);
        contractRepository.save(contract);
        contractService.addContract(contract);
        return "redirect:/contractPage";
    }
    /*
     * 查询所有模板并显示
     * 返回合同创建页面
     * */
    @GetMapping("/createContractPage")
    public String goCreateContractPage(HttpSession session, RedirectAttributes model, Model model1) throws IOException, RequestException {
        String username = (String) session.getAttribute("username");
        Individual individual = individualRepository.findByAccountId(username);
        if(individual == null) {
            model.addFlashAttribute("error", "请进行个人认证");
            return "redirect:/personCertificationPage";
        }
        List<ContractTemplate> templates = ctrepository.findAllByAccountId(username);
        System.out.println(templates.size());
        model1.addAttribute("template", templates);
        return "createContract";
    }
    /*
    * 查询所有待我签署的合同并显示
    * 返回合同列表界面
    * */
    @GetMapping("/contractPage")
    public String goContractPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) throws RequestException {
        String username = (String) session.getAttribute("username");
        Individual individual = individualRepository.findByAccountId(username);
        if(individual == null) {
            redirectAttributes.addFlashAttribute("error", "请进行个人认证");
            return "redirect:/personCertificationPage";
        }
        List<Contract> list1 = contractRepository.findContractBySign1(username);
        List<Contract> list2 = contractRepository.findContractBySign2(username);
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < list1.size(); ++i) list.add(list1.get(i).getContractId());
        for(int i = 0; i < list2.size(); ++i) list.add(list2.get(i).getContractId());
        List<Contract> lists = new ArrayList<>();
        for(int i = 0; i < list.size(); ++i) {
            Contract contract = contractService.queryContractByContractId(list.get(i));
            if(contract != null)
                lists.add(contract);
        }
        if(lists.size() > 0)
            model.addAttribute("lists", lists);
        return "contract";
    }
    /*
    * 合同签署信息上链，返回合同列表界面
    * */
    @PostMapping("/sign")
    public String sign(@RequestParam("password") String password,@RequestParam("idCard") String idCard,
                     @RequestParam("contractId") String contractId, HttpSession session, RedirectAttributes model) throws RequestException {
        String username = (String) session.getAttribute("username");
        Users users = usersRepository.findByAccountId(username);
        Individual individual = individualRepository.findByAccountId(username);
        int contractId1 = Integer.valueOf(contractId);
        Contract contract = contractService.queryContractByContractId(contractId1);
        int state = contract.getState();

        if(users.getPwd().equals(password) && individual.getIdCard().equals(idCard)){
             long time = System.currentTimeMillis();
             if(username.equals(contract.getSign1())) {
               //  System.out.println("甲方签署");
                 state |= 1;
                 contractService.setContractState(contractId1, state);
                 contractService.updateContractInfo(contractId1, 0, 1, time);
             }
             else {
              //   System.out.println("乙方签署");
                 state |= 2;
                 contractService.setContractState(contractId1, state);
                 contractService.updateContractInfo(contractId1, 1, 2, time);
             }
            if(state == 3) contractService.updateContractInfo(contractId1, 1, 3, time);
            model.addFlashAttribute("msg", "签署成功");
        }
        else model.addFlashAttribute("msg", "签署失败");
        return "redirect:/contractPage";
    }

    @PostMapping("/updateContract")
    public String updateContract(@RequestParam("updateId") String contractId,@RequestParam("updateName") String name,@RequestParam("updateContext") String text,
                                 @RequestParam("updateStart") String startDate,@RequestParam("updateEnd") String endDate, HttpSession session) throws RequestException {
        String username = (String) session.getAttribute("username");
        int contractId1=Integer.valueOf(contractId);
        contractService.updateContractText(contractId1, name,text,startDate,endDate);
        long time = System.currentTimeMillis();
        Contract contract = contractService.queryContractByContractId(contractId1);
        if(username.equals(contract.getSign1())){
            contractService.updateContractInfo(contractId1, 0, 4, time);
        }
        else{
            contractService.updateContractInfo(contractId1, 1, 4, time);
        }contractService.setContractState(contractId1, 0);
        return "redirect:/contractPage";
    }
    /*
    * 合同签署历史信息界面
    * */
    @GetMapping("/contractHistoryPage")
    public String goContractHistoryPage(@RequestParam("contractId") String contractId, Model model) throws RequestException {
        int id = Integer.valueOf(contractId);
        List<signInfo> list = contractService.querySignInfo(id);
        List<HistoryInfo> infos = new ArrayList<>();
        for(int i = 0; i < list.size(); ++i){
            String t = list.get(i).getName();
            t += list.get(i).getOp();
            if(list.get(i).getOp().equals("生效")) t = "合同生效";
            HistoryInfo info = new HistoryInfo();
            info.setText(t);
            info.setTime(list.get(i).getTime());
            infos.add(info);
        }
        model.addAttribute("infos", infos);
        model.addAttribute("contractId", contractId);
        return "showContractHistory";
    }

    @GetMapping("/myContractPage")
    public  String goMyContractPage(HttpSession session, Model model) throws RequestException {
        String username = (String) session.getAttribute("username");
        List<Contract> list = contractRepository.findContractByAccountId(username);
        List<Integer> list1 = new ArrayList<>();
        for(int i = 0; i < list.size(); ++i) list1.add(list.get(i).getContractId());

        List<Contract> lists = new ArrayList<>();

        for(int i = 0; i < list1.size(); ++i) {
            Contract contract = contractService.queryContractByContractId(list1.get(i));
            if(contract != null)
                lists.add(contract);
        }

        if(lists.size() > 0)
            model.addAttribute("lists", lists);
        return "myContract";
    }
    @ResponseBody
    @PostMapping("/remove_contract")
    public void removeContract(@RequestParam("id") int id,@RequestParam("sign1")String sign1, HttpSession session) throws RequestException {
        String username = (String) session.getAttribute("username");
        contractService.setContractState(id, 5);
        if(username.equals(sign1))
        contractService.updateContractInfo(id, 0,5, System.currentTimeMillis());
        else contractService.updateContractInfo(id, 1, 5, System.currentTimeMillis());
    //    System.out.println(id + " " + sign1);
    }
}
