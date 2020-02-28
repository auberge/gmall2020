package com.auberge.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.auberge.gmall.bean.SkuInfo;
import com.auberge.gmall.bean.SkuSaleAttrValue;
import com.auberge.gmall.bean.SpuSaleAttr;
import com.auberge.gmall.config.LoginRequire;
import com.auberge.gmall.service.ManageService;
import com.auberge.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;

    @RequestMapping("{skuId}.html")
    @LoginRequire(autoRedirect = true)
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, HttpServletRequest request) {
        //根据skuId获取数据
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        //查询销售属性和销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        //获取销售属性值id
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //遍历集合拼接字符串
        //将数据放入map中，然后将map转换为想要的json格式！
        //map.put() JSON.toJSONString(map)
 /*       for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {

            //什么时候拼接，什么时候停止拼接， 当本次循环的skuId与下次循环时的skuId不一致时，停止拼接。拼接到最后则停止拼接？
        }*/
        String key = "";
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if (key.length() > 0) {
                key += "|";
            }
            key += skuSaleAttrValue.getSaleAttrValueId();
            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
                map.put(key, skuSaleAttrValue.getSkuId());
                key = "";
            }
        }
        //将map转换成json字符串
        String valueSkuJson = JSON.toJSONString(map);
        request.setAttribute("valueSkuJson",valueSkuJson);
        request.setAttribute("spuSaleAttrList", spuSaleAttrList);
        request.setAttribute("skuInfo", skuInfo);
        listService.incrHotScore(skuId);
        return "item";
    }

}
