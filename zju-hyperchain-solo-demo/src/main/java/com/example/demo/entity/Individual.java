package com.example.demo.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data//自动生成缺失的getter和setter方法
@AllArgsConstructor//自动生成所有参数的构造器
@Entity
@NoArgsConstructor
public class Individual {
    @Id
    private String idCard;        //身份证号
    private String name;       //姓名
    private String accountId;   //账户id
    private String publicKey; //公钥
    private String privateKey; //私钥
}
