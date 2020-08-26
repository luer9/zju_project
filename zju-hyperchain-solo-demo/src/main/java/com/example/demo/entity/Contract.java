package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data//自动生成缺失的getter和setter方法
@AllArgsConstructor//自动生成所有参数的构造器
@Entity
@NoArgsConstructor
public class Contract {
   // @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    private int contractId; //合同id
    private String contractName; //合同名称
    private String template; //合同模板内容
    private String accountId;
    private String sign1; //甲方
    private String sign2; //乙方
    private String createDate; //合同创建时间
    private String startDate; //合同生效时间
    private String endDate; //合同截至时间
    private int state; //合同状态 0:待签署 1:甲方签署 2:乙方签署 3:生效 4:修改中 5：失效

}
