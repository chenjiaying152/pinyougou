package com.pinyougou.shop.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
public class UploadController {

    /**
     * 注入文件服务器的URL
     */
    @Value("${fileServerUrl}")
    private String fileServerUrl;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        //返回状态码
        data.put("status", 500);
        try {
            //1.获取文件对应的字节数组
            byte[] bytes = multipartFile.getBytes();

            //2.获取原文件名
            String filename = multipartFile.getOriginalFilename();

            //3.上传文件到FastDFS文件服务器
            //3.1 加载fastdfs-client文件, 得到的绝对路径
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();
            //3.2 初始化客户端全局对象
            ClientGlobal.init(path);
            //3.3创建存储客户端对象
            StorageClient storageClient = new StorageClient();
            //3.4 上传文件到FastDFS
            String[] arr = storageClient.upload_file(bytes, FilenameUtils.getExtension(filename), null);

            //3.5 拼接图片访问的URL http://192.168.12.131/ arr[0] / arr[1]
            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String str : arr) {
                url.append("/" + str);
            }


            data.put("status", 200);
            data.put("url", url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
