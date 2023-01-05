package com.hackyle.blog.business.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片水印工具
 */
public class WaterMarkUtils {
    // 水印透明度
    private static final float alpha = 0.4f;

    /**
     * 水印文字字体
     * 此种文字格式下，平均一个英文字母占比的width为23.45px，height为35px
     * <plaintext>
     * 如何测得？
     *  1.写一个HTML，<span style="font-family: 'Microsoft YaHei UI'; font-weight: bold; font-size: 35px">HACKYLE</span>
     *  2.在浏览器打开，查看到大小为：164.18 * 44.55px
     *  3.单个字符的宽度为：164.18/7 = 23.45px
     *  4.44.55px是行高，字体大小为35px
     *  </plaintext>
     */
    private static final Font font = new Font("Microsoft Yahei UI", Font.BOLD, 35);
    // 水印文字颜色
    private static final Color color = Color.red;

    @Test
    public void testMarkByPic() throws IOException {
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        BufferedInputStream markBis = new BufferedInputStream(new FileInputStream("C:\\Users\\KYLE\\Desktop\\mark.png"));

        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        markByPic(markBis, bis, bos, "png");
    }

    /**
     * 给图片加图片水印，水印坐落于右下侧，距离最右边距有40px，距离最下边距有40px
     * @param markPic 图片水印
     * @param sourceImgStream 待添加水印的图片
     * @param targetImgStream 图片输出流
     * @param imgType 图片文件格式，例如png、jpg
     * @throws IOException -
     */
    public static void markByPic(InputStream markPic, InputStream sourceImgStream, OutputStream targetImgStream, String imgType) throws IOException {
        //入参检查
        if(imgType == null || "".equals(imgType.trim())) {
            throw new IllegalArgumentException("The img type can't be null: imgType=" +imgType);
        }
        if(sourceImgStream == null || markPic == null) {
            throw new IllegalArgumentException("The image stream can't be null!");
        }

        //图片读入
        BufferedImage sourceImage = ImageIO.read(sourceImgStream);
        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();

        //图片太小，就不要添加水印了
        if(sourceImageHeight < 350 || sourceImageWidth < 650) {
            ImageIO.write(sourceImage, imgType, targetImgStream);
        }

        //读取水印图片
        BufferedImage markImage = ImageIO.read(markPic);
        int markWidth = markImage.getWidth();
        int markHeight = markImage.getHeight();

        //获取画笔
        Graphics2D graph = sourceImage.createGraphics();
        //设置对线段的锯齿状边缘处理
        graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graph.drawImage(sourceImage.getScaledInstance(sourceImageWidth, sourceImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

        //设置水印文字颜色
        graph.setColor(color);
        //设置水印文字Font
        graph.setFont(font);
        //设置水印文字透明度
        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        //文本水印，水印所在坐标
        int widthLocation = sourceImageWidth - 40 - markWidth; //水印距离最右边有40px，另外一个的是水印图片的宽度
        int heightLocation = sourceImageHeight - 40 - markHeight; //水印距离最下边有40px，另外一个是水印图片的长度
        graph.drawImage(markImage, widthLocation, heightLocation, null);

        //关闭画笔
        graph.dispose();

        //图片写出
        ImageIO.write(sourceImage, imgType, targetImgStream);
    }

    @Test
    public void testMarkByText() throws Exception {
        String source = "C:\\Users\\KYLE\\Desktop\\aa.png";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));

        String text = "HACKYLE";

        String target = "C:\\Users\\KYLE\\Desktop\\" +System.currentTimeMillis()+ ".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));

        markByText(text, bis, bos, "png");
    }

    /**
     * 给图片加文本水印，水印坐落于右下侧，距离最右边距有40px，距离最下边距有40px
     * @param text 文本水印
     * @param sourceImgStream 待添加水印的图片
     * @param targetImgStream 图片输出流
     * @param imgType 图片文件格式，例如png、jpg
     * @throws IOException -
     */
    public static void markByText(String text, InputStream sourceImgStream, OutputStream targetImgStream, String imgType) throws IOException {
        //入参检查
        if(text == null || "".equals(text.trim()) || imgType == null || "".equals(imgType.trim())) {
            throw new IllegalArgumentException("The parameters of image water mark can't be null: text=" +text+ ", imgType=" +imgType);
        }
        if(sourceImgStream == null) {
            throw new IllegalArgumentException("The image source stream can't be null!");
        }

        //图片读入
        BufferedImage sourceImage = ImageIO.read(sourceImgStream);
        int sourceImageWidth = sourceImage.getWidth();
        int sourceImageHeight = sourceImage.getHeight();

        //图片太小，就不要添加水印了
        if(sourceImageHeight < 350 || sourceImageWidth < 650) {
            ImageIO.write(sourceImage, imgType, targetImgStream);
        }

        //获取画笔
        Graphics2D graph = sourceImage.createGraphics();
        //设置对线段的锯齿状边缘处理
        graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graph.drawImage(sourceImage.getScaledInstance(sourceImageWidth, sourceImageHeight, Image.SCALE_SMOOTH), 0, 0, null);

        //设置水印文字颜色
        graph.setColor(color);
        //设置水印文字Font
        graph.setFont(font);
        //设置水印文字透明度
        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        //文本水印，水印所在坐标
        int widthLocation = sourceImageWidth - 40 - (int)(text.length()*23.45); //水印距离最右边有40px，另外的是字体的长度
        int heightLocation = sourceImageHeight - 40; //水印距离最下边有40px
        graph.drawString(text, widthLocation, heightLocation);

        //关闭画笔
        graph.dispose();

        //图片写出
        ImageIO.write(sourceImage, imgType, targetImgStream);
    }

}
