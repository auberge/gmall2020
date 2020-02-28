package com.auberge.gmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {
    @Value("${fileServer.url}") //@Value 使用的前提条件是当前类必须在spring容器中
    String fileUrl;
    //String ip="192.168.88.129";硬编码
    //http:192.168.88.129服务器的ip地址作为一个配置文件放入项目中
    //获取上传文件，需要使用springmvc技术
    @RequestMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrl=fileUrl;
        if (file != null) {
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
            //获取连接
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //获取上传文件名称
            String originalFilename = file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(originalFilename, ".");
            //上传图片
            //String[] upload_file = storageClient.upload_file(originalFilename, extName, null);
            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl+="/"+path;
            }
        }
        return imgUrl;
    }
}
