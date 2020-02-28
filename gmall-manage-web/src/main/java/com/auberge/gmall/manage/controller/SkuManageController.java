package com.auberge.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.SkuInfo;
import com.auberge.gmall.bean.SpuImage;
import com.auberge.gmall.bean.SpuSaleAttr;
import com.auberge.gmall.service.ManageService;
import com.auberge.gmall.bean.SkuLsInfo;
import com.auberge.gmall.service.ListService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;

    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(SpuSaleAttr spuSaleAttr) {
        return manageService.getSpuSaleAttrList(spuSaleAttr);
    }

    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return manageService.getSpuImageList(spuImage);
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
    }

    //上传一个商品，如果批量上传
    @RequestMapping("onSale")
    public void onSale(String skuId) {
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //给skuLsInfo赋值
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        //属性拷贝
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        listService.saveSkuLsInfo(skuLsInfo);
    }
}
