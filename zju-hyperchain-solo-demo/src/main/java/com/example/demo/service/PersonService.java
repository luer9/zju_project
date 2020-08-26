package com.example.demo.service;

import cn.hyperchain.sdk.exception.RequestException;
import com.example.demo.entity.Enterprise;
import com.example.demo.entity.Individual;

import java.io.IOException;

public interface PersonService {
    public boolean addPersonInfo(Individual individual) throws IOException, RequestException;
    public Individual queryPersonInfo(String id) throws IOException, RequestException;
    public boolean addEnterpriseInfo(Enterprise enterprise) throws RequestException;
    public Enterprise queryEnterpriseInfo(String id) throws RequestException;
}
