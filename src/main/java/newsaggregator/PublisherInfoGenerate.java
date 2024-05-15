package newsaggregator;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class PublisherInfoGenerate {
    public static void main(String[] args) {
        PublisherInfoGenerate publisherInfoGenerate = new PublisherInfoGenerate();
        try {
            publisherInfoGenerate.generatePublisherInfo();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generatePublisherInfo() throws FileNotFoundException {
        File articleSource = new File("src/main/resources/rss/articleSources.txt");
        File result = new File("src/main/resources/json/publisherInfo.json");
        JSONArray publisherInfo = new JSONArray();
        try {
            Scanner scanner = new Scanner(articleSource);
            while (scanner.hasNextLine()){
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
                JSONObject publisher = new JSONObject();
                publisher.put("name", name);
                publisher.put("ref_name", refName);
                publisher.put("logo", "https://logo.clearbit.com/" + rssURL.getHost());
                publisherInfo.put(publisher);
            }
            scanner.close();
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("publishers", publisherInfo);
            System.out.println(resultJSON.toString(2));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Không tìm thấy file articlesSources.txt");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
