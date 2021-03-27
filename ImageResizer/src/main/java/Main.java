

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Напишите программу уменьшения изображений на основе проекта ImageResizer.
// Она должна запускать число потоков, равное количеству ядер процессора вашего компьютера.

public class Main
{
    private static final int NEW_WIDTH = 300;
    private static final String IMG_DIR ="src/main/compressed_image";
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {

        getImages();
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите путь к каталогу с картинками");
        String dstFolder = sc.nextLine();
        File secondFolder = new File(dstFolder);

        System.out.println("колличество ядер: "+ AVAILABLE_PROCESSORS);
        File[] files = secondFolder.listFiles();
        double middle = (double)files.length/AVAILABLE_PROCESSORS;
        int step = (int) Math.round(middle);
        System.out.println("длина масива изображений: "+files.length+"\nдлина одного массива: " + step);

//разбиваем массив по колличеству ядер и запускаем потоки
        int length = files.length;
        int place = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < AVAILABLE_PROCESSORS; i++){
            File []files1 = new File[step];
            System.arraycopy(files,place,files1,0,files1.length);
            ImageResizer resizer1 = new ImageResizer(files1, NEW_WIDTH,IMG_DIR,start);
            resizer1.start();
            place = place + step;
            length = length - step;
            if(length < step){
                File[]files2 = new File[length];
                System.arraycopy(files,place,files2,0,files2.length);
                ImageResizer resizer2=new ImageResizer(files2, NEW_WIDTH,IMG_DIR,start);
                resizer2.start();
                break;
            }
        }
    }

    //парсим страницу c кошками для полученения картинок
    public static void getImages()throws IOException {
        try {
            Pattern p = Pattern.compile("https://.*");
            Document doc = Jsoup.connect("https://cattish.ru/breed/").userAgent("Chrome").get();
            Elements elements = doc.getElementsByTag("img");
            for(int i = 0; i < elements.size(); i++) {
                String absUrl = elements.get(i).attr("src");
                Matcher m = p.matcher(absUrl);
                if(m.find()) {
                    URL a = new URL(absUrl);
                    URLConnection connection = a.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    Files.copy(inputStream, new File("src/main/image/" + absUrl.substring(absUrl.lastIndexOf("/"))).toPath());
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}
