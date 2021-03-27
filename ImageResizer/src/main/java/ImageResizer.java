import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageResizer extends Thread {
    private File []files;
    private int newWidth;
    private String dstFolder;
    private long start;

    public ImageResizer(File[] files, int newWidth, String dstFolder,long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.dstFolder = dstFolder;
        this.start = start;
    }

    @Override
    public void run(){
        try
        {
            for(File file : files)
            {
                BufferedImage image = ImageIO.read(file);
                if(image == null&&image.getHeight() <= 100 || image.getWidth() <= 100) continue;
                image = Scalr.resize(image, Scalr.Method.SPEED,newWidth*2);//уменьшаем изображение без потери качества (библиотека imgscalr)
                image = Scalr.resize(image,Scalr.Method.QUALITY,newWidth);
                File newFile = new File(dstFolder + "/" + file.getName());
                ImageIO.write(image, "jpg", newFile);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(System.currentTimeMillis()-start);
    }
}
