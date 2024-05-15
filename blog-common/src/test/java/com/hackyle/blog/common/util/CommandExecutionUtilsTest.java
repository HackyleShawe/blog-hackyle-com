package com.hackyle.blog.common.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class CommandExecutionUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutionUtilsTest.class);

    @Test
    public void testExecuteCommand() throws Exception {
        boolean exeRes1 = CommandExecutionUtils.executeCommand("cmd.exe /c start notepad"); //"start notepad"异步打开记事本
        System.out.println(exeRes1);

        //先进入桌面目录，再执行命令
        boolean exeRes2 = CommandExecutionUtils.executeCommand("cmd.exe /c rename a.txt b.txt", new File("C:\\users\\kyle\\desktop"));
        System.out.println(exeRes2);
    }

    @Test
    public void testExecuteCommandAndExportResult() throws Exception {
        String command = "ifpconfig";

        File tmpFile = null;
        try {
            //使用临时文件暂存数据
            tmpFile = Files.createTempFile(command+"-", ".txt").toFile();
            boolean res = CommandExecutionUtils.executeCommandAndExportResult(command, null, tmpFile);
            System.out.println(res);

        } catch (IOException e) {
            LOGGER.error("命令'{}'执行出现异常：", command, e);
        } finally {
            //if(tmpFile != null) {
            //    tmpFile.delete(); //注意：不管怎样，最终需要删除临时文件
            //}
        }
    }
    /**
     * 实例：数据库备份
     */
    @Test
    public void testExecuteCommandAndExportResult4Database() throws Exception {
        String user = "root";
        String password = "hackyle";
        String databaseName = "blog_hackyle_com";

        String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 --databases " + databaseName;
        //+ " --opt --no-create-info --no-create-db --skip-extended-insert --skip-quick ";

        File tmpFile = null;
        try {
            //使用临时文件暂存备份数据
            tmpFile = Files.createTempFile(databaseName+"-", ".sql").toFile();
            boolean res = CommandExecutionUtils.executeCommandAndExportResult(command, null, tmpFile);
            System.out.println(res);

        } catch (IOException e) {
            LOGGER.error("命令'{}'执行出现异常：", command, e);
        } finally {
            if(tmpFile != null) {
                tmpFile.delete(); //注意：不管怎样，最终需要删除临时文件
            }
        }
    }


    /**
     * 从脚本文件流中恢复数据库
     */
    @Test
    public void testExecuteCommandAndImportData4Database() throws Exception {
        String user = "root";
        String password = "hackyle";
        String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 ";

        //executeCommandAndImportData(command, null, new File("C:\\Users\\KYLE\\AppData\\Local\\Temp\\blog_hackyle_com-12305770339089683412.sql"));

        CommandExecutionUtils.executeCommandAndImportData(command, null, new FileInputStream("C:\\Users\\KYLE\\AppData\\Local\\Temp\\blog_hackyle_com-12305770339089683412.sql"));
    }
}
