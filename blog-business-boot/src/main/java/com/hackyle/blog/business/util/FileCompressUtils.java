package com.hackyle.blog.business.util;

import java.io.*;
import java.util.zip.*;

public class FileCompressUtils {

    /**
     * ZIP压缩
     *
     * @param srcFilePath 带压缩的文件或者目录
     * @param targetDir 压缩后的文件存放位置
     * @return 压缩后的文件绝对路径
     */
    public static String compressFilesByZIP(String srcFilePath, String targetDir) throws IOException {
        //入参校验与矫正
        targetDir = parameterCheck(srcFilePath, targetDir);

        File srcFile = new File(srcFilePath);
        String outputFileName;
        if (srcFile.isDirectory()) {
            outputFileName = targetDir + ".zip"; //是文件夹，则以文件夹名作为压缩包名
        } else {
            outputFileName = targetDir + File.separator + srcFile.getName() + ".zip";
        }

        CheckedOutputStream checkedOutputStream = new CheckedOutputStream(new FileOutputStream(outputFileName), new Adler32());
        ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream);
        compressRecursion(srcFile, zipOutputStream, srcFile.getName()); //递归压缩
        zipOutputStream.close();

        return outputFileName;
    }

    /**
     * 压缩一个文件，如果是一个文件夹则递归压缩
     * @param file 当前文件、文件夹
     * @param zos 压缩到哪里
     * @param folderLevel 文件夹层级
     */
    private static void compressRecursion(File file, ZipOutputStream zos, String folderLevel) throws IOException {
        if(file.isFile()) {
            // 向zip输出流添加该文件的zip实体
            zos.putNextEntry(new ZipEntry(folderLevel));
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
            is.close();
            bis.close();
            //zos.closeEntry();

        } else { //是文件夹
            // 列出文件夹的子文件数组，包括文件夹和文件
            File[] files = file.listFiles();

            if (files == null || files.length == 0) { // 如果是空文件夹，也要添加到压缩包里
                zos.putNextEntry(new ZipEntry(folderLevel + "/"));
                // 因为空文件夹没有数据所以不需要向zip写入数据
                //zos.closeEntry();
            } else {
                for (File tmpFile : files) {
                    // 递归实现层级压缩
                    // 注意：path + "/" + file.getName()一定要加上原来路径才能保持原来层级关系
                    compressRecursion(tmpFile, zos, folderLevel + "/" + tmpFile.getName());
                }
            }
        }
    }

    /**
     * ZIP解压
     * @param zipFilePath zip文件绝对路径
     * @param targetDir 解压后的存放路径
     */
    public static String decompressFilesByZIP(String zipFilePath, String targetDir) throws IOException {
        //入参校验与矫正
        targetDir = parameterCheck(zipFilePath, targetDir);

        File zipFile = new File(zipFilePath);
        FileInputStream fileInputStream = new FileInputStream(zipFile);
        CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, new Adler32());
        ZipInputStream zipInputStream = new ZipInputStream(checkedInputStream);
        ZipEntry zipEntry;

        //输出位置
        String zipFilePureName = zipFile.getName().substring(0, zipFile.getName().indexOf("."));//切割出文件名，不要文件拓展名
        File outputDir = new File(targetDir + File.separator + zipFilePureName);
        if(!outputDir.mkdirs()) {
            throw new RuntimeException("The dir haven't Created!");
        }

        FileOutputStream fileOutputStream = null;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            fileOutputStream = new FileOutputStream(outputDir.getAbsolutePath() +File.separator+ zipEntry.getName());
            int len;
            byte[] bytes = new byte[1024];
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
            fileOutputStream.close();
        }
        zipInputStream.close();
        fileInputStream.close();

        return targetDir;
    }

    /**
     * 文件检查
     * @param srcPath 文件绝对路径
     * @param targetDir 目录绝对路径
     */
    private static String parameterCheck(String srcPath, String targetDir) {
        //入参校验
        if(srcPath == null || "".equals(srcPath)) {
            throw new IllegalArgumentException("The srcPath can't be null!");
        }
        File srcFile = new File(srcPath);
        if(!srcFile.exists()) {
            throw new IllegalArgumentException("The srcFile isn't exist!");
        }

        if(null == targetDir || "".equals(targetDir.trim())) {
            if(srcFile.isDirectory()) {
                return srcFile.getAbsolutePath();
            }
            return srcFile.getParent();
        }

        File tmpDir = new File(targetDir);
        if(!tmpDir.exists() || !tmpDir.isDirectory()) {
            if(!tmpDir.mkdirs()) {
                throw new RuntimeException("The targetDir haven't Created!");
            }
        }
        return tmpDir.getAbsolutePath();
    }
}
