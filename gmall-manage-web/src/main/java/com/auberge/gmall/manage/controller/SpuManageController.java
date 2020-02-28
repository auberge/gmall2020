package com.auberge.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.service.ManageService;
import com.auberge.gmall.bean.SpuInfo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class SpuManageController {
    @Reference
    private ManageService manageService;

    //http://localhost:8082/spuList?catalog3Id=780 实体类对象封装
    @RequestMapping("spuList")
    public List<SpuInfo> spuList(SpuInfo spuInfo) {
        return manageService.getSpuInfoList(spuInfo);
    }

    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        if (spuInfo!=null) {
            manageService.saveSpuInfo(spuInfo);
        }
    }
}
