package com.example.demo.repository;

import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface usersRepository extends JpaRepository<Users,String> {
    Users findByAccountId(String accountId);
}
