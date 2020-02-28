package com.auberge.gmall.service;

import com.auberge.gmall.bean.SkuLsParams;
import com.auberge.gmall.bean.SkuLsResult;
import com.auberge.gmall.bean.SkuLsInfo;

public interface ListService {
    /**
     * 保存数据到es中
     * @param skuLsInfo
     */
    void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    /**
     * 根据用户输入的条件检索数据
     * @return
     */
    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * 更新热度值
     * @param skuId
     */
    void incrHotScore(String skuId);
}
