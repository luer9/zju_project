package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data//自动生成缺失的getter和setter方法
@AllArgsConstructor//自动生成所有参数的构造器
@Entity
@NoArgsConstructor
public class ContractTemplate {
    @Id
    private Integer templateId;
    private String templateName;
    private String templateContext;
    private String templateType;
    private String accountId;
}
