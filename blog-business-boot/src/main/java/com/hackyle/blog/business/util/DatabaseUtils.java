package com.hackyle.blog.business.util;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DatabaseUtils {

    /**
     * 数据库备份
     * @param command 备份命令
     * @param backupFilePath 备份到那个文件里
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static void backup(String command, String backupFilePath) throws IOException {
        if(null == command || "".equals(command)) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        //String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 --databases " + databaseName;
        //+ " --opt --no-create-info --no-create-db --skip-extended-insert --skip-quick ";
        Process process = runtime.exec(command);
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter( new FileOutputStream(backupFilePath), StandardCharsets.UTF_8);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        inputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();

        //注意：不能使用以下方式导出，无法导出到文件中
        //String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 --databases " + databaseName
        //        + " > " + backupFilePath;
        //System.out.println(command);
        //Process process = runtime.exec(command);
        //process.waitFor();
        //InputStream is = process.getInputStream();
        //System.out.println(new String(is.readAllBytes())); //获取脚本执行过程，会显示所有导出脚本，获取InputStream流可以抓取导出数据
        //System.out.println("--------------------");
        //System.out.println(new String(process.getErrorStream().readAllBytes()));
    }

    @Test
    public void testBackup() {
        String user = "root";
        String password = "hackyle";
        String databaseName = "blog_hackyle_com_dev";

        String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 --databases " + databaseName;
        //+ " --opt --no-create-info --no-create-db --skip-extended-insert --skip-quick ";

        File tmpFile = null;
        try {
            //使用临时文件暂存备份数据
            //注意：不管怎样，最终需要删除临时文件
            tmpFile = Files.createTempFile(databaseName+"-", ".sql").toFile();
            String backupFilePath = tmpFile.getAbsolutePath();
            System.out.println(backupFilePath);

            backup(command, backupFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(tmpFile != null) {
                tmpFile.delete(); //删除该个临时文件
            }
        }

    }


    /**
     * 从脚本文件流中恢复数据库
     * @param command 恢复命令
     * @param restoreStream 恢复的数据流
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static void restore(String command, InputStream restoreStream) throws IOException {
        if(command == null || "".equals(command.trim())) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        //String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 ";
        Process process = runtime.exec(command);
        OutputStream outputStream = process.getOutputStream();
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(restoreFilePath), StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(restoreStream));

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        outputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();

        //注意：不能使用以下方式导入，无法成功导入
        //String command = "mysqldump -u" + user + " -p" + password + " --set-charset=utf8 "
        //        + " > " + backupFilePath;
        //System.out.println(command);
        //Process process = runtime.exec(command);
        //process.waitFor();
    }

    /**
     * 从脚本文件中恢复数据库
     * @param command 恢复命令
     * @param restoreFilePath 恢复的脚本文件路径
     * @exception IOException 工具类的异常统一向外部抛，由调用方处理
     */
    public static void restore(String command, String restoreFilePath) throws IOException {
        if(command == null || "".equals(command.trim())) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        //String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 ";
        Process process = runtime.exec(command);
        OutputStream outputStream = process.getOutputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(restoreFilePath), StandardCharsets.UTF_8));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            outputStreamWriter.write(str + "\r\n");
        }
        outputStreamWriter.flush();
        outputStream.close();
        bufferedReader.close();
        outputStreamWriter.close();
    }

    @Test
    public void testRestore() throws IOException {
        String user = "root";
        String password = "hackyle";
        //String databaseName = "blog_hackyle_com_dev";
        String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 ";

        restore(command, "C:\\Users\\KYLE\\AppData\\Local\\Temp\\blog_hackyle_com_dev-5670582013257829.sql");
    }

}
