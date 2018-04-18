package com.pinyougou.manager.controller;

import com.common.FastDFSClient;
import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file){
        if (file == null){
            return new Result(false,"上传文件为空!");
        }
        String originaFileName = file.getOriginalFilename();
        System.out.println(originaFileName);
        //截取文件后缀名
        String extName = originaFileName.substring(originaFileName.lastIndexOf(".") + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL + path;
            System.out.println(FILE_SERVER_URL+path);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }

}
