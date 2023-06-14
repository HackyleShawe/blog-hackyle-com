package com.hackyle.blog.business.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.service.SystemManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemManageController.class);

    @Autowired
    private SystemManageService systemManageService;

    @GetMapping("/systemStatus")
    public ApiResponse<Map<String, Object>> systemStatus() {
        Map<String, Object> systemStatusMap = systemManageService.systemStatus();

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), systemStatusMap);
    }

    /**
     * 数据库备份
     */
    @GetMapping("/databaseBackup")
    public void databaseBackup(@RequestParam("databaseName") String databaseName, HttpServletResponse response){
        try {
            if(StringUtils.isBlank(databaseName)) {
                response.setContentType("application/json;charset=UTF-8");
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage()));
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            File databaseBackupFilePath = systemManageService.databaseBackup(databaseName);
            if(databaseBackupFilePath == null) {
                response.setContentType("application/json;charset=UTF-8");
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage(), "数据库备份失败！"));
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            FileInputStream fis = new FileInputStream(databaseBackupFilePath);

            response.setContentType("application/octet-stream;charset=UTF-8");

            //因为是跨前后端分离，默认reponse header只能取到以下：Content-Language，Content-Type，Expires，Last-Modified，Pragma
            //要想获取到文件名，需要采取这种方式。Reference：https://www.cnblogs.com/liuxianbin/p/13035809.html
            response.setHeader("filename", URLEncoder.encode(databaseBackupFilePath.getName(), StandardCharsets.UTF_8));
            response.setHeader("Access-Control-Expose-Headers","filename");
            response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(databaseBackupFilePath.getName(), StandardCharsets.UTF_8));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(fis.readAllBytes());
            outputStream.flush();
            outputStream.close();
            fis.close();

            databaseBackupFilePath.delete();

        } catch (IOException e) {
            LOGGER.error("数据库备份时出现异常：", e);
        }
    }

    /**
     * 恢复数据库
     */
    @PostMapping(value = "/databaseRestore")
    public ApiResponse<String> databaseRestore(@RequestParam(value = "restoreSql", required = false) MultipartFile[] multipartFiles) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        //目前只支持恢复zip文件
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            //判定是否为zip压缩文件，目前只支持zip
            if((StringUtils.isNotBlank(fileName) && !fileName.substring(fileName.lastIndexOf(".")+1).equalsIgnoreCase("zip"))
                    || (StringUtils.isNotBlank(contentType) && !contentType.contains("zip")) ) {
                return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage(), "目前只支持从zip文件恢复");
            }
        }

        try {
            systemManageService.databaseRestore(multipartFiles);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("恢复数据库出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 文件夹的备份
     */
    @GetMapping("/dirBackup")
    public void dirBackup(@RequestParam("fileDirPath") String fileDirPath, HttpServletResponse response){
        try {
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();

            if(StringUtils.isBlank(fileDirPath)) {
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage()));
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            File fileDir = new File(fileDirPath);
            if(!fileDir.exists() || fileDir.isFile()) { //待备份的一定是一个图片文件夹，而非一个文件
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage(), "图片文件目录输入错误"));
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            File databaseBackupFilePath = systemManageService.dirBackup(fileDir);
            if(databaseBackupFilePath == null) {
                String respStr = JSON.toJSONString(ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage(), "文件夹备份失败！"));
                outputStream.write(respStr.getBytes(StandardCharsets.UTF_8));
                response.flushBuffer();
                outputStream.close();
                return;
            }

            FileInputStream fis = new FileInputStream(databaseBackupFilePath);
            response.setContentType("application/octet-stream;charset=UTF-8"); //重新设置要传输的文件类型

            //因为是跨前后端分离，默认reponse header只能取到以下：Content-Language，Content-Type，Expires，Last-Modified，Pragma
            //要想获取到文件名，需要采取这种方式。Reference：https://www.cnblogs.com/liuxianbin/p/13035809.html
            response.setHeader("filename", URLEncoder.encode(databaseBackupFilePath.getName(), StandardCharsets.UTF_8));
            response.setHeader("Access-Control-Expose-Headers","filename");
            response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(databaseBackupFilePath.getName(), StandardCharsets.UTF_8));
            outputStream.write(fis.readAllBytes());
            outputStream.flush();
            outputStream.close();
            fis.close();

            databaseBackupFilePath.delete(); //删除临时文件

        } catch (Exception e) {
            LOGGER.error("文件夹备份时出现异常：", e);
        }
    }

    /**
     * 文件夹的恢复
     * @param restoreDir 恢复到那个文件夹里
     */
    @PostMapping(value = "/dirRestore")
    public ApiResponse<String> dirRestore(@RequestParam(value = "restoreFileZip", required = true) MultipartFile[] multipartFiles,
                                          @RequestParam(value = "restoreDir", required = true) String restoreDir) {
        if(multipartFiles == null || multipartFiles.length < 1) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage());
        }

        //目前只支持恢复.tar.zip文件
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            if((StringUtils.isNotBlank(fileName) && !fileName.contains(".tar.zip"))) {
                return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage(), "目前只支持从zip文件恢复");
            }
        }

        try {
            systemManageService.dirRestore(multipartFiles, restoreDir);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("文件夹恢复时出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
