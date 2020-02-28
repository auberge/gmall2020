package com.auberge.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.*;
import com.auberge.gmall.service.ManageService;
import com.auberge.gmall.webutil.bean.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ManageController {
    @Reference
    private ManageService manageService;

    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }

    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return manageService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id) {
        return manageService.getAttrList(catalog3Id);
    }

    //将前台页面传递过来的json数据转换为对象
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
    }

    //    @RequestMapping("getAttrValueList")
//    public List<BaseAttrValue> getAttrValueList(String attrId) {
//        //select * from baseAttrValue where attrId = ?
//        return manageService.getAttrValueList(attrId);
//    }
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        //先通过attrId查询平台属性 select * from baseAttrValue where id = attrId
        BaseAttrInfo baseAttrInfo=manageService.getAttrInfo(attrId);
        //返回平台属性中的平台属性集合
        return baseAttrInfo.getAttrValueList();
    }

    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }
}
