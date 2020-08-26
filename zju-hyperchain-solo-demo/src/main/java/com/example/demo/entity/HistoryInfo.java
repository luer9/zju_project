package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor//自动生成所有参数的构造器
@NoArgsConstructor
public class HistoryInfo {
    private String text;
    private String time;
}
