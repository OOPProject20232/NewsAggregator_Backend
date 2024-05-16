package newsaggregator.script;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.database.MongoDB.MongoDBController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lớp PublisherInfoGenerate dùng để tạo thông tin của các nhà xuất bản từ file articleSources.txt
 * (LƯU Ý: Lớp này chỉ cần chạy 1 lần để tạo thông tin của các nhà xuất bản, tạo 2 lần sẽ gây ra lỗi. Nếu gặp phải lỗi, hãy drop collection).
 */
public class PublisherInfoGenerate {
    public static final Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) {
        try {
            File articleSource = new File("src/main/resources/rss/articleSources.txt");
            List<org.bson.Document> publisherList = new ArrayList<>();
            try {
                Scanner scanner = new Scanner(articleSource);
                while (scanner.hasNextLine()){
                    try {
                        String rssLink = scanner.nextLine();
                        URL rssURL = URI.create(rssLink).toURL();
                        System.out.println(rssURL);
                        String refName = rssURL.getHost().replace("www.", "").replace(".com", "");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) rssURL.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.connect();
                        int responseCode = httpURLConnection.getResponseCode();
                        System.out.println(responseCode);
                        InputStream inputStream = httpURLConnection.getInputStream();
                        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        Document document = documentBuilder.parse(inputStream);
                        Element channel = (Element) document.getElementsByTagName("channel").item(0);
                        String name = channel.getElementsByTagName("title").item(0).getTextContent();
                        System.out.println("=========");
                        System.out.println(name);
                        System.out.println(refName);
                        System.out.println("https://logo.clearbit.com/" + rssURL.getHost());
                        System.out.println("=========");
                        org.bson.Document doc = new org.bson.Document("name", name)
                                .append("ref_name", refName)
                                .append("logo", "https://logo.clearbit.com/" + rssURL.getHost());
                        publisherList.add(doc);
                    } catch (SSLHandshakeException | ConnectException e) {
                        System.out.println(e.getMessage());
                    }
                }
                scanner.close();
                try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
                    MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
                    MongoCollection<org.bson.Document> collection = db.getCollection("articles.publishers");
                    collection.insertMany(publisherList);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Không tìm thấy file articlesSources.txt");
            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
            MongoDBController dbController = new MongoDBController();
            dbController.get("articles.publishers", "src/main/resources/rss/publishers.json");
        } catch (FileNotFoundException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
