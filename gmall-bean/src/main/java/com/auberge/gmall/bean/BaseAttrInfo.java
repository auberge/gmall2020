package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) //获取主键自增
    String id;
    @Column
    String attrName;
    @Column
    String catalog3Id;
    //baseAttrValue 集合
    @Transient //表示当前字段不是表中的字段，是业务需要使用的字段
    List<BaseAttrValue> attrValueList;
}
