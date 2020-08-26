package com.example.demo.repository;

import com.example.demo.entity.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

public interface contractTemplateRepository extends JpaRepository<ContractTemplate,Integer> {
    ContractTemplate findBytemplateId(Integer templateId);
    List<ContractTemplate> findAllByAccountId(String id);

    @Modifying
    @Transactional//删除需要成为事物的一部分
    void deleteContractTemplateByTemplateId(Integer id);
}
