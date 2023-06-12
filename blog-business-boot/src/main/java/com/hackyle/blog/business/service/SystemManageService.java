package com.hackyle.blog.business.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface SystemManageService {
    Map<String,Object> systemStatus();

    File databaseBackup(String databaseName) throws IOException;

    void databaseRestore(MultipartFile[] multipartFiles) throws IOException ;
}
