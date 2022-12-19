package com.hackyle.blog.business.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    List<String> fileUpload(MultipartFile[] multipartFiles) throws Exception;

}
