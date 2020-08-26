package com.example.demo.repository;

import com.example.demo.entity.Individual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface individualRepository extends JpaRepository<Individual,String>{
    Individual findByAccountId(String accountId);
}
