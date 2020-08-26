package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@AllArgsConstructor//自动生成所有参数的构造器
@NoArgsConstructor
public class signInfo {
    private String name;
    private String op;
    private String time;
}
