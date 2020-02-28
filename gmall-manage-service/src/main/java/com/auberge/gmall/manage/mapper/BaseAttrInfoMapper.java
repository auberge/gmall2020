package com.auberge.gmall.manage.mapper;

import com.auberge.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    /**
     * 根据三级分类Id查询平台属性值集合
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);
    /**
     * 平台属性值Id查询数据
     * @param valueIds //多个参数
     * @return
     */
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}
