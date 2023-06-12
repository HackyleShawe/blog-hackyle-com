package com.hackyle.blog.business.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.hackyle.blog.business.common.constant.OperationTypeEnum;
import com.hackyle.blog.business.service.ConfigurationService;
import com.hackyle.blog.business.service.SystemManageService;
import com.hackyle.blog.business.util.DatabaseUtils;
import com.hackyle.blog.business.util.FileCompressUtils;
import com.hackyle.blog.business.util.FileHandleUtils;
import com.hackyle.blog.business.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class SystemManageServiceImpl implements SystemManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemManageServiceImpl.class);
    private final DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    private ConfigurationService configurationService;

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
    public File databaseBackup(String databaseName){
        String command = "mysqldump -u" + databaseUser + " -p" + databasePassword + " --set-charset=utf8 --databases " + databaseName;

        File tmpFile = null;
        String databaseBackupFilePath = null;

        try {
            //使用临时文件暂存备份数据
            //注意：不管怎样，最终需要删除临时文件
            tmpFile = Files.createTempFile("DatabaseBackup-", ".sql").toFile();
            String backupFilePath = tmpFile.getAbsolutePath();

            DatabaseUtils.backup(command, backupFilePath);
            databaseBackupFilePath = FileCompressUtils.compressFilesByZIP(tmpFile.getAbsolutePath(), tmpFile.getParent());
        } catch (IOException e) {
            LOGGER.error("数据库备份-写入文件时异常：", e);
        } finally {
            if(tmpFile != null) {
                tmpFile.delete(); //删除该个临时文件
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
        String sqlFileName = "";

        for (MultipartFile multipartFile : multipartFiles) {
            InputStream inputStream = multipartFile.getInputStream();
            String command = "mysql -u" + databaseUser + " -p" + databasePassword + " --default-character-set=utf8 ";
            DatabaseUtils.restore(command, inputStream);

            sqlFileName += multipartFile.getOriginalFilename();
        }

        LOGGER.info("数据库恢复完成-sqlFileName={}", sqlFileName);
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
