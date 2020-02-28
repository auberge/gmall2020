package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class UserAddress implements Serializable {
    @Column
    @Id
    String id;
    @Column
    String userAddress;
    @Column
    String userId;
    @Column
    String consignee;
    @Column
    String phoneNum;
    @Column
    String isDefault;
}
