package com.example.demo.repository;

import com.example.demo.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface contractRepository extends JpaRepository <Contract,String> {
    public List<Contract> findContractBySign1(String sign);
    public List<Contract> findContractBySign2(String sign);
    public List<Contract> findContractByAccountId(String id);
}
