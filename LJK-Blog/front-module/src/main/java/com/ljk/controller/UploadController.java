package com.ljk.controller;

import com.ljk.domain.ResponseResult;
import com.ljk.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseResult ossUpload(MultipartFile img){
        return uploadService.ossUpload(img);
    }
}
