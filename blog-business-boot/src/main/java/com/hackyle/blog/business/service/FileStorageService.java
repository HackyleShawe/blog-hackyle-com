package com.hackyle.blog.business.service;

import com.hackyle.blog.business.dto.FileStorageDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.qo.FileStorageQo;
import com.hackyle.blog.business.vo.FileStorageVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;

public interface FileStorageService {
    List<String> fileUpload(MultipartFile[] multipartFiles) throws Exception;

    void saveImg4ArticleAdd(ArticleEntity articleEntity);

    void saveImg4ArticleUpdate(ArticleEntity articleEntity);

    void fileDelete(FileStorageDto fileLink);

    void fileDelete(List<String> fileLinkList);

    boolean fileUpdate(FileStorageDto storageDto);

    FileInputStream download(String fileLink) throws Exception;

    PageResponseDto<FileStorageVo> fetchList(PageRequestDto<FileStorageQo> pageRequest);

}
