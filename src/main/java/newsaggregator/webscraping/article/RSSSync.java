package newsaggregator.webscraping.article;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.net.*;

import javax.xml.parsers.*;

public class RSSSync {

    //Methods

    public static void getNewUpdate(String urlString, String cacheURIString){
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Path file = Path.of(cacheURIString);
            if (file.toFile().exists()) {
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                ZonedDateTime lastModified = attr.lastModifiedTime().toInstant().atZone(ZoneOffset.UTC);
                connection.setRequestMethod("HEAD");
                connection.setRequestProperty("If-Modified-Since", lastModified.format(DateTimeFormatter.RFC_1123_DATE_TIME));
                connection.connect();
                int responseCode = connection.getResponseCode();
                System.out.println(responseCode);
                if (responseCode == 304) {
                    return;
                }
            }
            else {
                file.toFile().getParentFile().mkdirs();
                file.toFile().createNewFile();
                connection.setRequestMethod("GET");
                connection.connect();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(cacheURIString)) {
                    InputStream iStream = url.openStream();
                    byte buffer[] = new byte[1024];
                    int length;
                    while ((length = iStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                    }
                    fileOutputStream.close();
                } catch (Exception e) {
                    System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
                }
            }
        }
        catch (Exception e){
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }
}