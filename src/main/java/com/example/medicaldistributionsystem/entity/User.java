package com.example.medicaldistributionsystem.entity;

import lombok.Data;

@Data
public class User {
    private Long userId;

    private String nickName;

    private String loginName;

    private String passwordMd5;

    private String introduceSign;


}