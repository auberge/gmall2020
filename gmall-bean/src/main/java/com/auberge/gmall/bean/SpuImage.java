package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class SpuImage implements Serializable {
    @Column
    @Id
    String id;
    @Column
    String spuId;
    @Column
    String imgName;
    @Column
    String imgUrl;
}
