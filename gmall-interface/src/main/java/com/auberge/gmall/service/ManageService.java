package com.auberge.gmall.service;

import com.auberge.gmall.bean.*;

import java.util.List;

public interface ManageService {
    /**
     * 获取所有的一级分类数据
     *
     * @return
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 根据一级分类Id查询二级分类数据
     * select * from baseCatalog2 where catalogId=?
     *
     * @param catalog1Id
     * @return
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 根据二级分类Id查询三级分类数据
     * select * from baseCatalog3 where catalogId=?
     *
     * @param catalog2Id
     * @return
     */
    List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     * 根据三级分类查询平台属性集合
     *
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * 保存平台属性数据
     *
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性Id查询平台属性值集合
     *
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     * 根据平台属性Id查询平台属性对象
     *
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrInfo(String attrId);

    /**
     * 根据spuInfo对象属性获取spuInfo集合
     *
     * @param spuInfo
     * @return
     */
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    /**
     * 获取所有销售属性数据
     *
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存SpuInfo
     *
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId获取spu销售属性列表
     *
     * @param spuSaleAttr
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(SpuSaleAttr spuSaleAttr);

    /**
     * 根据spuId获取spu图片集合
     *
     * @param spuImage
     * @return
     */
    List<SpuImage> getSpuImageList(SpuImage spuImage);

    /**
     * 保存skuInfo数据
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 根据skuId查询skuInfo
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     * 根据skuId和spuId查询销售属性值集合
     * @param skuInfo
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * 根据spuId查询销售属性值集合
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 根据平台属性值Id查询
     * @param attrValueIdList
     * @return
     */
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
