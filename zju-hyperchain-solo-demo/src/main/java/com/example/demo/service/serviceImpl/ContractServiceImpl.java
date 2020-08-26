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
import cn.hyperchain.sdk.service.ServiceManager;
import cn.hyperchain.sdk.transaction.Transaction;
import com.example.demo.entity.Contract;
import com.example.demo.entity.signInfo;
import com.example.demo.service.ContractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.sasl.SaslServer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContractServiceImpl implements ContractService {
    public static  final  String DEFAULT_URL="192.168.1.13:8081";
    public static String PASSWORD="123456";
    public static String contrcatAddress= null;

    // 1. build provider manager
    public static DefaultHttpProvider defaultHttpProvider = new DefaultHttpProvider.Builder().setUrl(DEFAULT_URL).build();
    public static ProviderManager providerManager = ProviderManager.createManager(defaultHttpProvider);
    // 2. build service
    public static cn.hyperchain.sdk.service.ContractService contractService = ServiceManager.getContractService(providerManager);
    public static AccountService accountService = ServiceManager.getAccountService(providerManager);
    // 3. build transaction
    public static Account account = accountService.genAccount(Algo.SMAES, PASSWORD);
    public static InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/contract/TestContract.bin");
    public static InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/contract/TestContract.abi");
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
        }
        System.out.println("合同合约地址：" + contrcatAddress);
    }
    //创建合同
    @Override
    public void addContract(Contract contract) throws RequestException {

        FuncParams params = new FuncParams();
        params.addParams(contract.getContractId());
        params.addParams(contract.getContractName());
        params.addParams(contract.getTemplate());
        params.addParams(contract.getCreateDate());
        params.addParams(contract.getStartDate());
        params.addParams(contract.getEndDate());
        params.addParams(contract.getSign1());
        params.addParams(contract.getSign2());
        params.addParams(contract.getState());
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "contractStorage(uint32,string,string,string,string,string,string,string,uint32)",abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
    }
    //查询合同是否上链
    @Override
    public boolean isContractExist(Integer id) throws RequestException {
        FuncParams params = new FuncParams();
        params.addParams(1);
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "isContractExist(uint32)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        String ret = receiptResponse.getRet();
        byte[] bytes = ByteUtil.fromHex(ret);
        List<?> decode = abi.getFunction("isContractExist(uint32)").decodeResult(bytes);
        String result = decode.get(0).toString();
        if("false".equals(result))
            return false;
        return  true;
    }
    //查询合同内容
    @Override
    public Contract queryContractByContractId(int id) throws RequestException {
        if(!isContractExist(id) ) return  null;
        FuncParams params = new FuncParams();
        params.addParams(id);
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "contractQuery(uint32)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        String ret = receiptResponse.getRet();
        byte[] bytes = ByteUtil.fromHex(ret);
        List<?> decode = abi.getFunction("contractQuery(uint32)").decodeResult(bytes);
       /* for(Object result : decode){
            if(result.getClass().isArray()){
                int l = Array.getLength(result);System.out.println(l);
                for(int i = 0; i < l; ++i){
                    System.out.println(Array.get(result, i));
                }
            }
        }*/
        Contract contract = new Contract();
        contract.setContractId(id);
        contract.setContractName(decode.get(0).toString());
        contract.setTemplate(decode.get(1).toString());
        contract.setCreateDate(decode.get(2).toString());
        contract.setStartDate(decode.get(3).toString());
        contract.setEndDate(decode.get(4).toString());
        contract.setSign1(decode.get(5).toString());
        contract.setSign2(decode.get(6).toString());
        contract.setState(Integer.valueOf(decode.get(7).toString()));
        return contract;
    }
    //更新合同 签署信息
    @Override
    public void updateContractInfo(int id, int signName, int op, long time) throws RequestException {
        FuncParams params = new FuncParams();
        params.addParams(id);
        params.addParams(signName);
        params.addParams(op);
        params.addParams(time);
       // System.out.println(id + " " + signName + " " + op + " " + time);
        Transaction transaction3 = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "contractUpdate(uint32,uint32,uint32,uint64)", abi, params).build();
        transaction3.sign(account);
        ReceiptResponse receiptResponse3 = contractService.invoke(transaction3).send().polling();
         System.out.println("签署信息上链："+id + " " + signName + " " + op + " " + time);
    }
    //查询签署信息
    @Override
    public List<signInfo> querySignInfo(int id) throws RequestException {
        FuncParams params = new FuncParams();
        params.addParams(id);
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "queryStateInfo(uint32)", abi, params).build();
        transaction.sign(account);
        ReceiptResponse receiptResponse = contractService.invoke(transaction).send().polling();
        String ret = receiptResponse.getRet();
        byte[] bytes = ByteUtil.fromHex(ret);
        List<?> decode = abi.getFunction("queryStateInfo(uint32)").decodeResult(bytes);
        System.out.println(decode.size());
        List<signInfo> list = new ArrayList<>();
        for(Object result : decode){
            if(result.getClass().isArray()){
                int l = Array.getLength(result);
                for(int i = 0; i < l; i+=3){
                    signInfo signInfo = new signInfo();
                    int t1 = Integer.valueOf(Array.get(result, i).toString());
                    if(t1 == 0)signInfo.setName("甲方");
                    else signInfo.setName("乙方");
                    int t2 = Integer.valueOf(Array.get(result, i+1).toString());;
                    if(t2 == 0)
                        signInfo.setOp("创建");
                    else if(t2 == 1 || t2 == 2)
                        signInfo.setOp("签署合同");
                    else if(t2 == 3)
                        signInfo.setOp("生效");
                    else if(t2 == 4) signInfo.setOp("修改合同");
                    else signInfo.setOp("解除合同");
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                    long t3 = Long.valueOf(Array.get(result, i+2).toString());
                    Date date = new Date(t3);
                    String time = formatter.format(date).toString();
                    signInfo.setTime(time);

                    list.add(signInfo);
                }
            }
           // System.out.println("--------------");
        }
        return list;
    }
    // 修改合同内容
    @Override
    public void updateContractText(int id, String name, String text, String start, String end) throws RequestException {
        FuncParams params6 = new FuncParams();
        params6.addParams(id);
        params6.addParams(name);
        params6.addParams(text);
        params6.addParams(start);
        params6.addParams(end);
        Transaction transaction6 = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "contractTextUpdate(uint32,string,string,string,string)", abi, params6).build();
        transaction6.sign(account);
        contractService.invoke(transaction6).send().polling();
    }
    //设置合同状态
    @Override
    public void setContractState(int id, int state) throws RequestException {
        FuncParams params = new FuncParams();
        System.out.println(id + " " + state);
        params.addParams(id);
        params.addParams(state);
        Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).invoke(contrcatAddress, "contractUpdateState(uint32,uint32)", abi, params).build();
        transaction.sign(account);
        contractService.invoke(transaction).send().polling();
    }
}
