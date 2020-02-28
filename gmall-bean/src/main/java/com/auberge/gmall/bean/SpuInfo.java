package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
public class SpuInfo implements Serializable {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    @Column
    String spuName;
    @Column
    String description;
    @Column
    String catalog3Id;
    @Transient
    List<SpuSaleAttr> spuSaleAttrList;
    @Transient
    List<SpuImage> spuImageList;
}
