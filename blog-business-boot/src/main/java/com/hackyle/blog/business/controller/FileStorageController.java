package com.hackyle.blog.business.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.common.pojo.MinioFile;
import com.hackyle.blog.business.service.MinioService;
import com.hackyle.blog.business.vo.FileVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MinioService minioService;

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload")
    public ApiResponse<List<FileVo>> uploadFile(@RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }
        List<String> fileObjects = null;
        try {
            fileObjects = minioService.fileUpload(multipartFiles);

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
        List<String> fileObjects = null;
        try {
            fileObjects = minioService.fileUpload(multipartFiles);
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

        List<String> fileObjects = null;
        try {
            fileObjects = minioService.fileUpload(multipartFiles);
            if(fileObjects.size() == 1) {
                JSONObject object = new JSONObject();
                object.put("location", fileObjects.get(0));
                return object.toJSONString();
            } else {
                return JSON.toJSONString(ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage()));
            }

        } catch (Exception e) {
            LOGGER.error("上传图片出现异常：", e);
            return JSON.toJSONString(ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage()));
        }
    }

    /**
     * 文件删除
     */
    @GetMapping("/del")
    public ApiResponse<String> fileDelete(@RequestParam("fileName") String fileName) {
        if(null == fileName || "".equals(fileName.trim())) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        try {
            boolean delFlag = minioService.fileDelete(fileName);
            if(delFlag) {
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }
        }catch (Exception e) {
            LOGGER.error("文件删除出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 获取文件详细信息
     */
    @GetMapping("/fetch")
    public ApiResponse<MinioFile> fileDetail(@RequestParam("fileName") String fileName) {
        if(null == fileName || "".equals(fileName.trim())) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        try {
            boolean delFlag = true; //minioService.fileDetail(fileName);
            if(delFlag) {
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }
        }catch (Exception e) {
            LOGGER.error("文件删除出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
