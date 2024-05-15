package com.hackyle.blog.common.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaterMarkUtilsTest {
    @Test
    public void testMarkByPic() throws IOException {
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        BufferedInputStream markBis = new BufferedInputStream(new FileInputStream("C:\\Users\\KYLE\\Desktop\\mark.png"));

        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        WaterMarkUtils.markByPic(markBis, bis, bos, "png");
    }

    @Test
    public void testMarkByText() throws Exception {
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        String text = "HACKYLE";

        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        WaterMarkUtils.markByText(text, bis, bos, "png");
    }


}
