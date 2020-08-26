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
public class Enterprise{
    @Id
    private String regisNumber;//注册号
    private String enterpriseName;//企业名称
    private String location;//单位所在地
    private String businessScope;//经营范围
    private String date;//营业期限
    private String organizationCode;//组织机构代码
    private String legalPerson;//法人姓名
    private String legalPersonCode;//法人身份证号

    private String accountId;//账户id
    private String publicKey;//公钥
    private String privateKey;//密钥

}
