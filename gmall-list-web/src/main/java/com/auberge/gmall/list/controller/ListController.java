package com.auberge.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.*;
import com.auberge.gmall.service.ListService;
import com.auberge.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
//    @ResponseBody
    public String listData(SkuLsParams skuLsParams, HttpServletRequest request) {
        skuLsParams.setPageSize(1);
        SkuLsResult skuLsResult = listService.search(skuLsParams);
//        return JSON.toJSONString(skuLsResult);
        //显示商品数据
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();
        //平台属性，平台属性值
        //获取平台属性值Id集合
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        //通过平台属性值Id查询平台属性名称，平台属性值名 称
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(attrValueIdList);
        //http://list.gmall.com/list.html?keyword=手机&valueId=13
        //编写一个方法来判断url后面的参数条件
        String urlParam = makeUrlParam(skuLsParams);

        //定义一个面包屑集合
        List<BaseAttrValue> baseAttrValueList = new ArrayList();

        //使用迭代器
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            //平台属性
            BaseAttrInfo baseAttrInfo = iterator.next();
            //获取平台属性值集合对象
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //获取skuLsParams.getValueId 循环对比
                if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
                    for (String valueId : skuLsParams.getValueId()) {
                        if (valueId.equals(baseAttrValue.getId())) {
                            //如果平台属性值相同，则将集合中的数据移除
                            iterator.remove();

                            BaseAttrValue baseAttrValueED = new BaseAttrValue();
                            //将平台属性值的名称改成面包屑
                            baseAttrValueED.setValueName(baseAttrInfo.getAttrName() + ":" + baseAttrValue.getValueName());
                            //将用户点击的平台属性值Id传递到makeUrlParam方法中，重新制作返回的urlParam
                            String newUrlParam = makeUrlParam(skuLsParams, valueId);
                            baseAttrValueED.setUrlParam(newUrlParam);
                            baseAttrValueList.add(baseAttrValueED);
                        }
                    }
                }
            }
        }
        System.out.println(baseAttrValueList);
        //保存分页的数据
        request.setAttribute("pageNo",skuLsParams.getPageNo());
        request.setAttribute("totalPages",skuLsResult.getTotalPage());

        //保存一个检索的关键字
        request.setAttribute("keyword", skuLsParams.getKeyword());
        //保存面包屑
        request.setAttribute("baseAttrValueList", baseAttrValueList);
        //保存到作用域
        request.setAttribute("urlParam", urlParam);
        //保存平台属性值集合
        request.setAttribute("baseAttrInfoList", baseAttrInfoList);
        //保存商品集合
        request.setAttribute("skuLsInfoList", skuLsInfoList);
        return "list";
    }

    /**
     * 判断Url后面具体有哪些参数
     *
     * @param skuLsParams
     * @param excludeValueIds //点击面包屑时，获取的平台属性值Id
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams, String... excludeValueIds) {
        String urlParam = "";
        //根据keyword
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            //http://list.gmall.com/list.html?keyword=手机
            urlParam += "keyword=" + skuLsParams.getKeyword();
        }
        //http://list.gmall.com/list.html?keyword=手机&catalog3Id=61
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            //如果有多个参数则拼接&符号
            if (urlParam.length() > 0) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + skuLsParams.getCatalog3Id();
        }
        //平台属性值Id
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            //循环遍历
            //http://list.gmall.com/list.html?keyword=手机&catalog3Id=61&valueId=83
            for (String valueId : skuLsParams.getValueId()) {
                if (excludeValueIds != null && excludeValueIds.length > 0) {
                    //获取点击面包屑时的平台属性值Id
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals(valueId)){
                        continue;
                    }
                }
                if (urlParam.length() > 0) {
                    urlParam += "&";
                }
                urlParam += "valueId=" + valueId;
                System.out.println(urlParam);
            }
        }
        //返回制作好的参数
        return urlParam;
    }
}
