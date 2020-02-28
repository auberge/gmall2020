package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class UserInfo implements Serializable {
    // 通用mapper 的注解
    @Id // 表示主键
    @Column // 普通字段列
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 获取数据库主键自增！ mysql  GenerationType.IDENTITY  oracle :GenerationType.AUTO
    String id;
    @Column
    String loginName;
    @Column
    String nickName;
    @Column
    String passwd;
    @Column
    String name;
    @Column
    String phoneNum;
    @Column
    String email;
    @Column
    String headImg;
    @Column
    String userLevel;
}