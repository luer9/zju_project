package com.example.demo.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data//自动生成缺失的getter和setter方法
@AllArgsConstructor
@NoArgsConstructor
@Entity//表明是一个实体类
public class Users {

    @Id
    private String accountId;//账号
    // user_id  userId
    private String pwd;//密码
    private String phone;
    public boolean accountType; //账户类型：0：个人用户 1：企业用户


}
