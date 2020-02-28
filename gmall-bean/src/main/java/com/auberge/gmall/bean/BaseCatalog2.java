package com.auberge.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class BaseCatalog2 implements Serializable {
    @Id
    @Column
    String id;
    @Column
    String name;
    @Column
    String catalog1Id;
}
