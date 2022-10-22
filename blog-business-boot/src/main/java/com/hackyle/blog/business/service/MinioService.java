package com.hackyle.blog.business.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinioService {
    List<String> fileUpload(MultipartFile[] multipartFiles) throws Exception;

    boolean fileDelete(String fileName) throws Exception;

}
