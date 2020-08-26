package com.example.demo.service.serviceImpl;

import cn.hyperchain.sdk.account.Account;
import cn.hyperchain.sdk.account.Algo;
import cn.hyperchain.sdk.common.solidity.Abi;
import cn.hyperchain.sdk.common.utils.ByteUtil;
import cn.hyperchain.sdk.common.utils.FileUtil;
import cn.hyperchain.sdk.common.utils.FuncParams;
import cn.hyperchain.sdk.exception.RequestException;
import cn.hyperchain.sdk.provider.DefaultHttpProvider;
import cn.hyperchain.sdk.provider.ProviderManager;
import cn.hyperchain.sdk.response.ReceiptResponse;
import cn.hyperchain.sdk.service.AccountService;
import cn.hyperchain.sdk.service.ContractService;
import cn.hyperchain.sdk.service.ServiceManager;
import cn.hyperchain.sdk.transaction.Transaction;
import com.example.demo.entity.Enterprise;
import com.example.demo.entity.Individual;
import com.example.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

public class PersonServiceImpl implements PersonService {
    public static  final  String DEFAULT_URL="192.168.1.13:8081";
    public static String PASSWORD="123456";
    public static String contrcatAddress=null;

       // 1. build provider manager
    public static DefaultHttpProvider defaultHttpProvider = new DefaultHttpProvider.Builder().setUrl(DEFAULT_URL).build();
    public static ProviderManager providerManager = ProviderManager.createManager(defaultHttpProvider);
       // 2. build service
    public static ContractService contractService = ServiceManager.getContractService(providerManager);
    public static AccountService accountService = ServiceManager.getAccountService(providerManager);
       // 3. build transaction
    public static Account account = accountService.genAccount(Algo.SMAES, PASSWORD);
    public static InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/accountinfo/Sign.bin");
    public static InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/accountinfo/Sign.abi");
    public static String bin;
    public static String abiStr;
    public static Abi abi;

    //初始化合约信息
    static {
        try {
            bin = FileUtil.readFile(inputStream1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            abiStr = FileUtil.readFile(inputStream2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        abi = Abi.fromJson(abiStr);
        if(contrcatAddress == null){
            Transaction transaction =new Transaction.EVMBuilder(account.getAddress()).deploy(bin).build();
            transaction.sign(account);
            ReceiptResponse receiptResponse = null;
            try {
                receiptResponse = contractService.deploy(transaction).send().polling();
            } catch (RequestException e) {
                e.printStackTrace();
            }
            contrcatAddress = receiptResponse.getContractAddress();

        }  System.out.println("个人信息合约地址：" + contrcatAddress);

    }
    //添加个人信息
    @Override
    public boolean addPersonInfo(Individual individual) throws IOException, RequestException {
        if(queryPersonInfo(individual.getIdCard()) != null) return false;

        FuncParams params =new FuncParams();
        params.addParams(individual.getIdCard()); //身份证号
        params.addParams(individual.getName()); //姓名
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "newPerson(string,string)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();

        return true;
    }
    //查询个人信息
    @Override
    public Individual queryPersonInfo(String id) throws IOException, RequestException {

        FuncParams params =new FuncParams();
        params.addParams(id); //身份证号
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "selectPerson(string)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        String ret = receiptResponse.getRet();
        byte[] fromHex = ByteUtil.fromHex(ret);
        List<?> decodeResult = abi.getFunction("selectPerson(string)").decodeResult(fromHex);
       /* for(Object result : decodeResult){
            System.out.println(result.toString());
        }*/
        String s = decodeResult.get(2).toString();
        if(decodeResult == null || "".equals(s)) return  null;
        Individual individual = new Individual();
        individual.setIdCard(decodeResult.get(1).toString());
        individual.setName(decodeResult.get(2).toString());
       // System.out.println(individual);
        return individual;
    }
    //添加企业信息
    @Override
    public boolean addEnterpriseInfo(Enterprise enterprise) throws RequestException {
        if(queryEnterpriseInfo(enterprise.getRegisNumber()) != null) return false;
        FuncParams params =new FuncParams();
        params.addParams(enterprise.getRegisNumber()); //企业代码
        params.addParams(enterprise.getEnterpriseName()); //企业姓名
        params.addParams(enterprise.getRegisNumber());//注册号
        params.addParams(enterprise.getLocation());//企业所在地
        params.addParams(enterprise.getBusinessScope());//经营范围
        params.addParams(enterprise.getDate());//营业期限
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "newOrganization(string,string,string,string,string,string)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        return true;
    }
    //查询企业信息
    @Override
    public Enterprise queryEnterpriseInfo(String id) throws RequestException {
        FuncParams params =new FuncParams();
        params.addParams(id); //企业代码
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "selectOrganization(string)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        String ret = receiptResponse.getRet();
        byte[] fromHex = ByteUtil.fromHex(ret);
        List<?> decodeResult = abi.getFunction("selectOrganization(string)").decodeResult(fromHex);
        /*for(Object result : decodeResult){
            System.out.println(result.toString());
        }*/
        String s = decodeResult.get(2).toString();
        if(decodeResult == null || "".equals(s)) return  null;
        Enterprise enterprise = new Enterprise();
        enterprise.setOrganizationCode(decodeResult.get(1).toString());
        enterprise.setEnterpriseName(decodeResult.get(2).toString());
        enterprise.setRegisNumber(decodeResult.get(3).toString());
        enterprise.setLocation(decodeResult.get(4).toString());
        enterprise.setBusinessScope(decodeResult.get(5).toString());
        enterprise.setDate(decodeResult.get(6).toString());
        return enterprise;
    }
}
