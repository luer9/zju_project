package com.example.demo.service;

import cn.hyperchain.sdk.exception.RequestException;
import com.example.demo.entity.Contract;
import com.example.demo.entity.signInfo;

import java.util.List;

public interface ContractService {
    public void addContract(Contract contract) throws RequestException;
    public Contract queryContractByContractId(int id) throws RequestException;
    public boolean isContractExist(Integer id) throws RequestException;
    public void updateContractInfo(int id, int signname, int op, long time) throws RequestException;
    public void updateContractText(int id, String name, String text, String start, String end) throws RequestException;
    public void setContractState(int id, int state) throws RequestException;
    public List<signInfo>  querySignInfo(int id) throws RequestException;
}
