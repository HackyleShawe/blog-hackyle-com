package com.hackyle.blog.business.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FileStorageDto;
import com.hackyle.blog.business.qo.FileStorageQo;
import com.hackyle.blog.business.service.FileStorageService;
import com.hackyle.blog.business.vo.FileStorageVo;
import com.hackyle.blog.business.vo.FileVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 注意：歌曲、图片等数据不适合放于resources/static，因为程序一旦运行起来，static目录下的文件是不会变化的。
 * 只适合放置静态的代码数据：JS、CSS、HTML等
 * 歌曲、图片的资源适合放置于专门的服务器、存储桶
 */
@RestController
@RequestMapping("/file")
public class FileStorageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageController.class);
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload")
    public ApiResponse<List<FileVo>> uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }
        List<String> fileObjects;
        try {
            fileObjects = fileStorageService.fileUpload(multipartFiles);

            List<FileVo> fileVoList = new ArrayList<>();
            for (String fileObject : fileObjects) {
                FileVo fileVo = new FileVo();
                fileVo.setUrl(fileObject);
                fileVoList.add(fileVo);
            }

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), fileVoList);

        } catch (Exception e) {
            LOGGER.error("上传文件出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 上传图片
     */
    @PostMapping(value = "/uploadImg")
    public ApiResponse<List<String>> uploadImg(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }
        List<String> fileObjects;
        try {
            fileObjects = fileStorageService.fileUpload(multipartFiles);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), fileObjects);

        } catch (Exception e) {
            LOGGER.error("上传图片出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * TinyMCE富文本编辑器的上传图片专属接口
     * 格式要求：{ location : '/uploaded/image/path/image.png' }
     */
    @PostMapping("/uploadImgTinyMCE")
    public Object uploadImgTinyMCE(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return JSON.toJSONString(ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage()));
        }

        List<String> fileObjects;
        try {
            fileObjects = fileStorageService.fileUpload(multipartFiles);

            JSONObject object = new JSONObject();
            object.put("location", fileObjects.get(0));
            return object.toJSONString();

        } catch (Exception e) {
            LOGGER.error("TinyMCE富文本编辑器上传图片出现异常：", e);
            return JSON.toJSONString(ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage()));
        }
    }

    /**
     * 文件删除
     */
    @DeleteMapping("/del")
    public ApiResponse<String> fileDelete(@RequestBody ApiRequest<FileStorageDto> apiRequest) {
        FileStorageDto storageDto = apiRequest.getData();
        LOGGER.info("文件删除-入参storageDto={}", JSON.toJSONString(storageDto));

        if(null == storageDto.getFileLink() || "".equals(storageDto.getFileLink().trim())) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        try {
            fileStorageService.fileDelete(storageDto);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());

        }catch (Exception e) {
            LOGGER.error("文件删除出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), e.getMessage());
        }
    }


    /**
     * 文件修改
     */
    @PutMapping("/update")
    public ApiResponse<String> fileUpdate(@RequestBody ApiRequest<FileStorageDto> apiRequest) {
        FileStorageDto storageDto = apiRequest.getData();
        LOGGER.info("文件修改-入参storageDto={}", JSON.toJSONString(storageDto));

        if(null == storageDto.getId() ||
                null == storageDto.getFileLink() || "".equals(storageDto.getFileLink().trim())) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        try {
            boolean delFlag = fileStorageService.fileUpdate(storageDto);
            if(delFlag) {
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }

        }catch (Exception e) {
            LOGGER.error("文件修改出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam("fileLink") String fileLink, HttpServletResponse response) {
        try {
            String[] split = fileLink.split("/");
            String fileName = split[split.length-1];
            FileInputStream fis = fileStorageService.download(fileLink);
            if(null == fis) {
                response.setContentType("application/json;charset=UTF-8");
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage(), "文件不存在"));
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            //response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/octet-stream;charset=UTF-8");

            //因为是跨前后端分离，默认reponse header只能取到以下：Content-Language，Content-Type，Expires，Last-Modified，Pragma
            //要想获取到文件名，需要采取这种方式。Reference：https://www.cnblogs.com/liuxianbin/p/13035809.html
            response.setHeader("filename",URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setHeader("Access-Control-Expose-Headers","filename");

            response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(fis.readAllBytes());
            outputStream.flush();
            outputStream.close();
            fis.close();

            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        }catch (Exception e) {
            LOGGER.error("文件下载出现异常：", e);
            //return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取文件列表
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<FileStorageVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<FileStorageQo>> apiRequest) {
        PageRequestDto<FileStorageQo> pageRequest = apiRequest.getData();
        if(pageRequest == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequest = new PageRequestDto<>();
            pageRequest.setCurrentPage(1);
            pageRequest.setPageSize(10);
        }

        if(pageRequest.getCurrentPage() < 1) {
            pageRequest.setCurrentPage(1);
        }
        if(pageRequest.getPageSize() < 1) {
            pageRequest.setPageSize(10);
        }

        try {
            PageResponseDto<FileStorageVo> pageResponse = fileStorageService.fetchList(pageRequest);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponse);

        }catch (Exception e) {
            LOGGER.error("获取文件列表出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
