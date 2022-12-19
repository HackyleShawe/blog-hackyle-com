package com.hackyle.blog.business.service.impl;

import com.hackyle.blog.business.service.FileStorageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    /** 资源文件（图片、视频等）存放路径 */
    @Value("${file-storage-path}")
    private String fileStoragePath;

    @Value("${file-domain}")
    private String fileDomain;



    @Override
    public List<String> fileUpload(MultipartFile[] multipartFiles) throws Exception {

        List<String> fileObjects = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            InputStream inputStream = file.getInputStream(); //得到文件流
            String fileName = file.getOriginalFilename(); //文件名
            //String contentType = file.getContentType();  //类型
            long sourceSize = file.getSize();

            String pathSplit = pathSplitByDate();
            String nameByUUID = nameByUUID(fileName);
            File storagePathFile = storagePath(pathSplit);

            String fullFile = storagePathFile.getAbsolutePath() + File.separator + nameByUUID;
            FileOutputStream os = new FileOutputStream(fullFile);

            long transferSize = inputStream.transferTo(os);

            if(sourceSize != transferSize) {
                throw new RuntimeException("File Storage Fail!");
            }

            fileObjects.add(fileDomain + pathSplit + nameByUUID);
        }

        return fileObjects;
    }

    /**
     * 拼接完整的存储路径
     */
    private File storagePath(String pathSplit) {
        if(StringUtils.isBlank(fileStoragePath)) {
            throw new RuntimeException("The file-storage-path can't be null!");
        }

        File file = new File(fileStoragePath + pathSplit);

        if(!file.exists()) {
            if(file.mkdirs()) {
                LOGGER.info("The file-storage-path haven't exists, it have been created!");
            }
        }

        if(!file.isDirectory()) {
            throw new RuntimeException("The file-storage-path is not a directory!");
        }

        return file;
    }

    /**
     * 存储于MinIO中的文件命名：时间戳
     * @param originalFileName 原始名字
     * @return 时间戳转换后的新名字
     */
    private String nameByUUID(String originalFileName) {
        if(originalFileName == null || "".equals(originalFileName.trim())) {
            throw new IllegalArgumentException("文件名为空");
        }
        return UUID.randomUUID().toString().replaceAll("-", "") + originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 根据当时日期生成文件前缀的路径：/年/月/
     */
    private String pathSplitByDate() {
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        return String.format("/%s/%s/", localDate.getYear(), month < 10 ? "0"+month: String.valueOf(month));
    }

}
