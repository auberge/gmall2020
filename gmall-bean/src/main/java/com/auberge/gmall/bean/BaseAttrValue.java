package com.auberge.gmall.bean;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
public class BaseAttrValue implements Serializable {
    @Id
    @Column
    String id;
    @Column
    String valueName;
    @Column
    String attrId;

    //声明一个变量
    @Transient
    private String urlParam;
}
