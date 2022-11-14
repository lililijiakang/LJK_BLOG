package com.ljk.service;

import com.ljk.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    ResponseResult ossUpload(MultipartFile img);
}
