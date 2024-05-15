package com.hackyle.blog.business.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.hackyle.blog.common.constant.OperationTypeEnum;
import com.hackyle.blog.business.service.SystemManageService;
import com.hackyle.blog.common.util.CommandExecutionUtils;
import com.hackyle.blog.common.util.FileCompressUtils;
import com.hackyle.blog.common.util.FileHandleUtils;
import com.hackyle.blog.common.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.VirtualMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class SystemManageServiceImpl implements SystemManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemManageServiceImpl.class);
    private final DecimalFormat df = new DecimalFormat("0.00");

    @Value("${spring.datasource.username}")
    private String databaseUser;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Override
    public Map<String,Object> systemStatus() {
        Map<String,Object> systemStatusMap = new HashMap<>();
        try {
            SystemInfo si = new SystemInfo();
            OperatingSystem os = si.getOperatingSystem();
            HardwareAbstractionLayer hal = si.getHardware();
            // 系统信息
            systemStatusMap.put("sys", getSystemInfo(os));
            // cpu 信息
            systemStatusMap.put("cpu", getCpuInfo(hal.getProcessor()));
            // 内存信息
            systemStatusMap.put("memory", getMemoryInfo(hal.getMemory()));
            // 交换区信息
            systemStatusMap.put("swap", getSwapInfo(hal.getMemory()));
            // 磁盘
            systemStatusMap.put("disk", getDiskInfo(os));
            systemStatusMap.put("time", DateUtil.format(new Date(), "HH:mm:ss"));
        } catch (Exception e) {
            LOGGER.error("获取系统时出现异常：", e);
        }

        return systemStatusMap;
    }

    /**
     * 数据库备份
     */
    @Override
    public File databaseBackup(String databaseName) {
        //需要设置MySQL的bin目录到环境变量，否则会报错找不到mysqldump命令
        String command = "mysqldump -u" + databaseUser + " -p" + databasePassword + " --set-charset=utf8 --databases " + databaseName;

        File tmpFile = null;
        String databaseBackupFilePath = null;

        try { //这里使用try的目的是确保最终临时文件能被删除
            //使用临时文件暂存备份数据
            tmpFile = Files.createTempFile("DatabaseBackup-", ".sql").toFile();

            //导出数据库中的数据，并且重定向到一个临时文件中
            CommandExecutionUtils.executeCommandAndExportResult(command, null, tmpFile);

            //进行文件压缩
            databaseBackupFilePath = FileCompressUtils.compressFilesByZIP(tmpFile.getAbsolutePath(), tmpFile.getParent());
        } catch (IOException | InterruptedException e) {
            LOGGER.error("数据库备份-写入文件时异常：", e);
        } finally {
            if(tmpFile != null) {
                tmpFile.delete(); //注意：不管怎样，最终需要删除临时文件
            }
        }
        LOGGER.info("数据库备份-command={}，databaseBackupFilePath={}", command, databaseBackupFilePath);

        return databaseBackupFilePath == null ? null : new File(databaseBackupFilePath);
    }

    /**
     * 从SQL恢复数据库
     */
    @Transactional
    @Override
    public void databaseRestore(MultipartFile[] multipartFiles) throws IOException {
        StringBuilder sqlFileName = new StringBuilder();

        for (MultipartFile multipartFile : multipartFiles) {
            InputStream inputStream = multipartFile.getInputStream();

            File tmpFile = null;
            File decompressFileDir = null;
            try { //这里使用try的目的是确保最终临时文件能被删除
                tmpFile = Files.createTempFile("DatabaseRestore-", ".zip").toFile();

                //解压zip文件
                decompressFileDir = FileCompressUtils.decompressFilesByZIP(multipartFile.getOriginalFilename(), inputStream, tmpFile.getParent());
                File[] files = decompressFileDir.listFiles(File::isFile);
                if(files != null) {
                    for (File file : files) {
                        FileInputStream sqlFileStream = new FileInputStream(file);

                        //需要设置MySQL的bin目录到环境变量，否则会报错找不到mysql命令
                        String command = "mysql -u" + databaseUser + " -p" + databasePassword + " --default-character-set=utf8 ";

                        //执行数据库备份命令，并且加载流到备份命令中
                        CommandExecutionUtils.executeCommandAndImportData(command, null, sqlFileStream);

                        sqlFileName.append(multipartFile.getOriginalFilename()).append("  ");
                    }
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.error("从SQL恢复数据库时文件异常：", e);
            } finally {
                if(tmpFile != null) {
                    tmpFile.delete(); //注意：不管怎样，最终需要删除临时文件
                }
                if(decompressFileDir != null) {
                    decompressFileDir.delete();
                }
            }
        }

        LOGGER.info("数据库恢复完成-sqlFileName={}", sqlFileName.toString());
    }



    /**
     * 文件夹备份
     * @param fileDir 要备份那个目录
     */
    @Override
    public File dirBackup(File fileDir) throws Exception{
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().startsWith(OperationTypeEnum.WIN.getName())) {
            throw new RuntimeException("The Windows Can't Be Backup!");
        }

        String parentDir = fileDir.getParent();
        String dirName = fileDir.getName();
        String backName = dirName + ".tar";
        String command = "tar -cf " + dirName + ".tar " + dirName;
        String tarFilePath = parentDir + File.separator + backName;

        CommandExecutionUtils.executeCommand(command, fileDir.getParentFile());
        String fileZipFilePath = FileCompressUtils.compressFilesByZIP(tarFilePath, parentDir);
        LOGGER.info("备份文件夹压缩后fileZipFilePath={}，压缩前的打包文件tarFilePath={}，tarFilePath删除状态={}", fileZipFilePath, tarFilePath, new File(tarFilePath).delete());

        return new File(fileZipFilePath);
    }

    /**
     * 从压缩文件恢复文件夹
     */
    @Override
    public void dirRestore(MultipartFile[] multipartFiles, String restoreDir) throws Exception{
        String restoreFileName = "";

        for (MultipartFile multipartFile : multipartFiles) {
            InputStream inputStream = multipartFile.getInputStream();

            String fileName = StringUtils.isBlank(multipartFile.getOriginalFilename()) ?
                    System.currentTimeMillis()+"tar.zip" : multipartFile.getOriginalFilename();
            File tmpFile = new File(restoreDir + File.separator + fileName);
            if(!tmpFile.getParentFile().exists()) {
                tmpFile.getParentFile().mkdirs();
            }

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            //接收tar.zip文件流到restoreDir目录
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
            //FileOutputStream fileOutputStream = new FileOutputStream(restoreDir + File.separator + fileName);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bos.close();
            bis.close();

            //解压
            CommandExecutionUtils.executeCommand("unzip " + fileName, tmpFile.getParentFile());
            //解包
            CommandExecutionUtils.executeCommand("tar -xvf " + fileName.substring(0, fileName.lastIndexOf(".zip"))
                    + tmpFile.getParentFile(), tmpFile.getParentFile());

            restoreFileName += multipartFile.getOriginalFilename();
        }

        LOGGER.info("文件夹恢复完成-恢复至restoreDir={}，restoreFileName={}", restoreDir, restoreFileName);
    }


    /**
     * 获取磁盘信息
     */
    private Map<String,Object> getDiskInfo(OperatingSystem os) {
        Map<String,Object> diskInfo = new LinkedHashMap<>();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        String osName = System.getProperty("os.name");
        long available = 0, total = 0;
        for (OSFileStore fs : fsArray){
            // windows 需要将所有磁盘分区累加，linux 和 mac 直接累加会出现磁盘重复的问题，待修复
            if(osName.toLowerCase().startsWith(OperationTypeEnum.WIN.getName())) {
                available += fs.getUsableSpace();
                total += fs.getTotalSpace();
            } else {
                available = fs.getUsableSpace();
                total = fs.getTotalSpace();
                break;
            }
        }
        long used = total - available;
        diskInfo.put("total", total > 0 ? FileHandleUtils.getSize(total) : "?");
        diskInfo.put("available", FileHandleUtils.getSize(available));
        diskInfo.put("used", FileHandleUtils.getSize(used));
        if(total != 0){
            diskInfo.put("usageRate", df.format(used/(double)total * 100));
        } else {
            diskInfo.put("usageRate", 0);
        }
        return diskInfo;
    }

    /**
     * 获取交换区信息
     */
    private Map<String,Object> getSwapInfo(GlobalMemory memory) {
        Map<String,Object> swapInfo = new LinkedHashMap<>();
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        long total = virtualMemory.getSwapTotal();
        long used = virtualMemory.getSwapUsed();
        swapInfo.put("total", FormatUtil.formatBytes(total));
        swapInfo.put("used", FormatUtil.formatBytes(used));
        swapInfo.put("available", FormatUtil.formatBytes(total - used));
        if(used == 0){
            swapInfo.put("usageRate", 0);
        } else {
            swapInfo.put("usageRate", df.format(used/(double)total * 100));
        }
        return swapInfo;
    }

    /**
     * 获取内存信息
     */
    private Map<String,Object> getMemoryInfo(GlobalMemory memory) {
        Map<String,Object> memoryInfo = new LinkedHashMap<>();
        memoryInfo.put("total", FormatUtil.formatBytes(memory.getTotal()));
        memoryInfo.put("available", FormatUtil.formatBytes(memory.getAvailable()));
        memoryInfo.put("used", FormatUtil.formatBytes(memory.getTotal() - memory.getAvailable()));
        memoryInfo.put("usageRate", df.format((memory.getTotal() - memory.getAvailable())/(double)memory.getTotal() * 100));
        return memoryInfo;
    }

    /**
     * 获取Cpu相关信息
     */
    private Map<String,Object> getCpuInfo(CentralProcessor processor) {
        Map<String,Object> cpuInfo = new LinkedHashMap<>();
        cpuInfo.put("name", processor.getProcessorIdentifier().getName());
        cpuInfo.put("package", processor.getPhysicalPackageCount() + "个物理CPU");
        cpuInfo.put("core", processor.getPhysicalProcessorCount() + "个物理核心");
        cpuInfo.put("coreNumber", processor.getPhysicalProcessorCount());
        cpuInfo.put("logic", processor.getLogicalProcessorCount() + "个逻辑CPU");
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 默认等待300毫秒...
        long time = 300;
        Util.sleep(time);
        long[] ticks = processor.getSystemCpuLoadTicks();
        while (Arrays.toString(prevTicks).equals(Arrays.toString(ticks)) && time < 1000){
            time += 25;
            Util.sleep(25);
            ticks = processor.getSystemCpuLoadTicks();
        }
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
        cpuInfo.put("used", df.format(100d * user / totalCpu + 100d * sys / totalCpu));
        cpuInfo.put("idle", df.format(100d * idle / totalCpu));
        return cpuInfo;
    }

    /**
     * 获取系统相关信息,系统、运行天数、系统IP
     */
    private Map<String,Object> getSystemInfo(OperatingSystem os){
        Map<String,Object> systemInfo = new LinkedHashMap<>();
        // jvm 运行时间
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        Date date = new Date(time);
        // 计算项目运行时间
        String formatBetween = DateUtil.formatBetween(date, new Date(), BetweenFormatter.Level.HOUR);
        // 系统信息
        systemInfo.put("os", os.toString());
        systemInfo.put("day", formatBetween);
        systemInfo.put("ip", IpUtils.getPublicIpv4());
        return systemInfo;
    }

    //private void systemBasciInfo() throws Exception {
    //    //本机IP
    //    String ip = InetAddress.getLocalHost().getHostAddress();
    //
    //    Runtime runtime = Runtime.getRuntime();
    //    //JVM可以使用的总内存
    //    long jvmTotalMemory = runtime.totalMemory();
    //    //JVM可以使用的剩余内存
    //    long jvmFreeMemory = runtime.freeMemory();
    //    //JVM可以使用的处理器个数
    //    int jvmAvailableProcessors = runtime.availableProcessors();
    //
    //    Properties props = System.getProperties();
    //    //Java的运行环境版本
    //    String javaVersion = props.getProperty("java.version");
    //    //Java的运行环境供应商
    //    String javaVendor = props.getProperty("java.vendor");
    //    //Java供应商的URL
    //    String javaVendorUrl = props.getProperty("java.vendor.url");
    //
    //    //Java的虚拟机实现版本
    //    String javaVmVersion = props.getProperty("java.vm.version");
    //    //Java的虚拟机实现供应商
    //    String javaVmVendor = props.getProperty("java.vm.vendor");
    //    //Java的虚拟机实现名称
    //    String javaVmName = props.getProperty("java.vm.name");
    //
    //    //操作系统的名称
    //    String osName = props.getProperty("os.name");
    //    //操作系统的构架
    //    String osArch = props.getProperty("os.arch");
    //    //操作系统的版本
    //    String osVersion = props.getProperty("os.version");
    //}
}
