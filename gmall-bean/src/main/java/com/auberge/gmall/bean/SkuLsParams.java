package com.auberge.gmall.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuLsParams implements Serializable {
    String catalog3Id;
    //keyword=skuName
    String keyword;
    String[] valueId;
    int pageNo=1;
    int pageSize=20;
}
