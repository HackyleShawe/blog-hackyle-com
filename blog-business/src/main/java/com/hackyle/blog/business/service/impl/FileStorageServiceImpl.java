package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.dto.FileStorageDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.entity.FileStorageEntity;
import com.hackyle.blog.business.mapper.FileStorageMapper;
import com.hackyle.blog.business.qo.FileStorageQo;
import com.hackyle.blog.business.service.FileStorageService;
import com.hackyle.blog.business.vo.FileStorageVo;
import com.hackyle.blog.common.util.PaginationUtils;
import com.hackyle.blog.common.util.WaterMarkUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    /** 资源文件（图片、视频等）存放路径 */
    @Value("${file-storage-path}")
    private String fileStoragePath;

    @Value("${file-domain}")
    private String fileDomain;

    /** 图片水印的文本 */
    @Value("${water-mark-text}")
    private String waterMarkText;

    @Autowired
    private FileStorageMapper fileStorageMapper;


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

            String targetFile = storagePathFile.getAbsolutePath() + File.separator + nameByUUID;
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
            //如果是图片，则需要加水印，然后再写出到指定位置
            if("jpg".equalsIgnoreCase(fileType) || "png".equalsIgnoreCase(fileType)
                    || "jpeg".equalsIgnoreCase(fileType) || "bpm".equalsIgnoreCase(fileType)) {
                WaterMarkUtils.markByText(waterMarkText, inputStream, outputStream, fileType);

            } else {
                long transferSize = inputStream.transferTo(outputStream);
                if(sourceSize != transferSize) {
                    throw new RuntimeException("File Storage Fail!");
                }
            }

            //关闭流，否则删除访问文件时显示被占用，导致删除失败
            inputStream.close();
            outputStream.close();

            fileObjects.add(fileDomain + pathSplit + nameByUUID);
        }

        //每次上传图片，就落库
        Integer inserted = fileStorageMapper.batchInsertImg(fileObjects);
        LOGGER.info("图片上传保存-fileObjects={},inserted={}", JSON.toJSONString(fileObjects), inserted);

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
        return String.format("%s/%s/", localDate.getYear(), month < 10 ? "0"+month: String.valueOf(month));
    }


    /**
     * 文章新增时保存文章主体内容中的图片信息
     */
    public void saveImg4ArticleAdd(ArticleEntity articleEntity) {
        if(StringUtils.isBlank(articleEntity.getContent())) {
            return; //没有文章内容，无须下一步操作
        }

        Set<String> imgSet = getImgByArticleContent(articleEntity.getContent());
        if(imgSet.size() < 1) {
            return; //新增文章时，没有图片，无须进行下一步操作
        }

        //表tb_file_storage表的article_uri可能有值（以前的图片，更新），可能没有值（新上传的图片，新添加）
        List<FileStorageEntity> newFileList = fileStorageMapper.selectByArticleUriIsNull(); //获取到article_uri为空的记录
        //List<FileStorageEntity> oldFileList = fileStorageMapper.selectByArticleUri(articleEntity.getUri()); //获取该文章以前的图片
        if(newFileList.size() < 1) {
            return;
        }

        //过滤出文章内容中的图片
        List<FileStorageEntity> existsFile = newFileList.stream().filter(ele -> imgSet.contains(ele.getFileLink())).collect(Collectors.toList());
        if(existsFile.size() > 0) {
            List<Long> existsFileIds = existsFile.stream().map(FileStorageEntity::getId).collect(Collectors.toList());
            Integer inserted = fileStorageMapper.updateArticleUriById(articleEntity.getUri(), existsFileIds);
            LOGGER.info("新插入的图片存入ArticleURI-imgSet={}，newFileList={}，existsFile={}，inserted={}",
                    JSON.toJSONString(imgSet), JSON.toJSONString(newFileList), JSON.toJSONString(existsFile), inserted);
        }

    }

    /**
     * 文章更新时保存、更新文章主体内容中的图片信息
     */
    public void saveImg4ArticleUpdate(ArticleEntity articleEntity) {
        if(StringUtils.isBlank(articleEntity.getContent())) {
            return; //没有文章内容，无须下一步操作
        }

        //先检查一下是否有新增图片
        saveImg4ArticleAdd(articleEntity);

        Set<String> imgSet = getImgByArticleContent(articleEntity.getContent());

        //表tb_file_storage表的article_uri可能有值（以前的图片，更新），可能没有值（新上传的图片，新添加）
        //List<FileStorageEntity> newFileList = fileStorageMapper.selectByArticleUriIsNull(); //获取到article_uri为空的记录
        List<FileStorageEntity> oldFileList = fileStorageMapper.selectByArticleUri(articleEntity.getUri()); //获取该文章以前的图片
        LOGGER.info("文章更新操作ArticleURI-oldFileList={}", JSON.toJSONString(oldFileList));
        if(oldFileList.size() < 1) {
            return;
        }

        //文章内容中的图片不在oldFileList中，说明文章中不含该图片，应该将其删除
        oldFileList.removeIf(fileEntity -> imgSet.contains(fileEntity.getFileLink()));
        if(oldFileList.size() > 0) {
            List<Long> notExists = oldFileList.stream().map(FileStorageEntity::getId).collect(Collectors.toList());
            int deleted = fileStorageMapper.deleteBatchIds(notExists);
            LOGGER.info("文章更新操作ArticleURI-imgSet={}，oldFileList.removeIf={}，notExists={}，deleted={}",
                    JSON.toJSONString(imgSet), JSON.toJSONString(oldFileList), JSON.toJSONString(notExists), deleted);

            fileDelete(oldFileList.stream().map(FileStorageEntity::getFileLink).collect(Collectors.toList()));
        }

    }

    /**
     * 获取文章内容中的图片
     */
    private Set<String> getImgByArticleContent(String articleContent) {
        Set<String> imgSet = new HashSet<>(); //存储图片链接
        if(StringUtils.isBlank(articleContent)) {
            return imgSet;
        }

        Document doc = Jsoup.parse(articleContent);
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String imgLink = img.attr("src");
            if(StringUtils.isNotBlank(imgLink)) {
                imgSet.add(imgLink);
            }
        }

        return imgSet;
    }


    @Override
    public void fileDelete(FileStorageDto storageDto) {
        String filePath = storageDto.getFileLink().substring(fileDomain.length());
        String fullPath = fileStoragePath + filePath;
        File file = new File(fullPath);

        if(!file.exists()) {
            throw new RuntimeException("文件不存在！");
        }

        if(!file.delete()) {
            throw new RuntimeException("文件删除失败！");
        }

        int del = fileStorageMapper.deleteById(storageDto.getId());
        LOGGER.info("文件删除-storageDto={}，del={}，fullPath={}", JSON.toJSONString(storageDto), del, fullPath);
    }

    public void fileDelete(List<String> fileLinkList) {
        if(CollectionUtils.isEmpty(fileLinkList)) {
            return;
        }

        for (String link : fileLinkList) {
            String filePath = link.substring(fileDomain.length());
            String fullPath = fileStoragePath + filePath;
            File file = new File(fullPath);

            if(file.exists() && file.delete()) {
                LOGGER.info("文件删除成功-fullPath={}", fullPath);
            } else {
                LOGGER.info("文件删除失败-fullPath={}", fullPath);
            }
        }
    }

    @Override
    public boolean fileUpdate(FileStorageDto storageDto) {
        FileStorageEntity fileEntity = new FileStorageEntity();
        fileEntity.setId(storageDto.getId());
        if(StringUtils.isNotBlank(storageDto.getArticleUri())) {
            fileEntity.setArticleUri(storageDto.getArticleUri());
        }
        fileEntity.setFileLink(storageDto.getFileLink());

        int update = fileStorageMapper.updateById(fileEntity);
        return update == 1;
    }

    @Override
    public FileInputStream download(String fileLink) throws Exception {
        String filePath = fileLink.substring(fileDomain.length());
        String fullPath = fileStoragePath + filePath;
        File file = new File(fullPath);

        if(file.exists() && file.isFile()) {
            return new FileInputStream(file);
        }

        return null; //文件不存在
    }

    @Override
    public PageResponseDto<FileStorageVo> fetchList(PageRequestDto<FileStorageQo> pageRequest) {
        QueryWrapper<FileStorageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        //组装查询条件
        FileStorageQo fileStorageQo = pageRequest.getCondition();
        if(fileStorageQo != null) {
            if(StringUtils.isNotBlank(fileStorageQo.getArticleUri())) {
                queryWrapper.lambda().like(FileStorageEntity::getArticleUri, fileStorageQo.getArticleUri());
            }
            if(StringUtils.isNotBlank(fileStorageQo.getFileLink())) {
                queryWrapper.lambda().like(FileStorageEntity::getFileLink, fileStorageQo.getFileLink());
            }
            if(null != fileStorageQo.getTimeRange() && fileStorageQo.getTimeRange().length == 2) {
                Date[] timeRange = fileStorageQo.getTimeRange();
                queryWrapper.lambda().between(FileStorageEntity::getUpdateTime, timeRange[0], timeRange[1]);
            }
            if(null != fileStorageQo.getEmptyLink() && Boolean.TRUE == fileStorageQo.getEmptyLink()) {
                queryWrapper.lambda().eq(FileStorageEntity::getArticleUri, "");
                //queryWrapper.lambda().and(ele ->
                //        ele.isNull(FileStorageEntity::getArticleUri)
                //        .or()
                //        .eq(FileStorageEntity::getArticleUri, "")
                //);
            }
        }
        queryWrapper.lambda().orderByDesc(FileStorageEntity::getCreateTime);

        //分页操作
        Page<FileStorageEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequest, FileStorageEntity.class);
        Page<FileStorageEntity> resultPage = fileStorageMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询文件列表-入参-pageRequestDto={},出参-resultPage.getRecords()={}", JSON.toJSONString(paramPage), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, FileStorageVo.class);
    }

}
