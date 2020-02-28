package com.auberge.gmall.manage.mapper;

import com.auberge.gmall.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    //根据spuId查询数据
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);

}
