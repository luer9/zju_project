package com.example.demo.repository;

import com.example.demo.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface enterpriseRepository extends JpaRepository<Enterprise,String> {//把实体类和数据库表建立联系
    Enterprise findEnterpriseByAccountId(String username);
}
