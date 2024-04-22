package newsaggregator.webscraping.article;

import newsaggregator.model.Article;
import newsaggregator.webscraping.Scraper;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Lớp RSSReader thực hiện việc đọc file XML từ các nguồn RSS được lưu trữ trong file webSources.txt
 * và trả về một danh sách các bài báo được lưu trữ trong các file XML này
 * @author Trần Quang Hưng
 */
public class RSSReader extends Scraper {

    //Methods

    @Override
    public void crawl() {
        System.out.println("Đang lấy dữ liệu từ các nguồn RSS...");
        List<Article> articleList = new ArrayList<>();
        try {
            File newsList = new File("src/main/resources/rssdata/webSources.txt");
            Scanner newsListScanner = new Scanner(newsList);
            while (newsListScanner.hasNextLine()) {
                String urlString = newsListScanner.nextLine();
                String domainString = URI.create(urlString).getHost();
                RSSSync.getNewUpdate(urlString, "src/main/resources/rssdata/tmp-cache/%s.xml".formatted(domainString));
                RSSReader rssReader = new RSSReader();
                List<Article> currentArticleList = rssReader.parseXML("src/main/resources/rssdata/tmp-cache/%s.xml".formatted(domainString), domainString);
                articleList.addAll(currentArticleList);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setContentList(articleList);
        System.out.println("Đã lấy dữ liệu từ các nguồn RSS...");
    }

    private List<Article> parseXML(String URIString, String domainString) {
        List<Article> currentArticleList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(URIString);
            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Node item = items.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) item;
                        // Post
                        Article currentArticle = new Article(
                                getGuid(elem),
                                getLink(elem),
                                getSource(domainString),
                                "article",
                                getTitle(elem),
                                getSummary(elem),
                                getDetailedContent(elem),
                                getDate(elem),
                                getAuthor(domainString, elem),
                                getThumbnail(elem),
                                getCategories(elem)
                        );
                        currentArticleList.add(currentArticle);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return currentArticleList;
    }

    private String getThumbnail(Element elem) {
        try {
            List<String> thumbnailTagList = new ArrayList<>(Arrays.asList("media:thumbnail", "media:content", "enclosure", "image"));
            for (String thumbnailTag : thumbnailTagList) {
                if  (elem.getElementsByTagName(thumbnailTag).item(0) != null) {
                    if (thumbnailTag.equals("image")) {
                        NodeList nodeList = elem.getElementsByTagName("image").item(0).getChildNodes();
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Node node = nodeList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;
                                return element.getElementsByTagName("url").item(0).getTextContent();
                            }
                        }
                    }
                    return elem.getElementsByTagName(thumbnailTag).item(0).getAttributes().getNamedItem("url").getTextContent();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getDetailedContent(Element elem) {
        try {
            if (elem.getElementsByTagName("content:encoded").item(0) != null &&
                    !(elem.getElementsByTagName("content:encoded").item(0).getTextContent().isEmpty())) {
                return Jsoup.parse(elem.getElementsByTagName("content:encoded").item(0).getTextContent()).text();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Jsoup.parse(elem.getElementsByTagName("description").item(0).getTextContent()).text();
    }

    private List<String> getCategories(Element elem) {
        ArrayList<String> categories = new ArrayList<>();
        try {
            for (int j = 0; j < elem.getElementsByTagName("category").getLength(); j++) {
                String category = elem.getElementsByTagName("category").item(j).getTextContent();
                if (category.isBlank()) {
                    continue;
                }
                categories.add(category.toLowerCase());
            }
            return categories;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getAuthor(String domainString, Element elem) {
        try {
            if (elem.getElementsByTagName("dc:creator").item(0) != null) {
                return elem.getElementsByTagName("dc:creator").item(0).getTextContent();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return getSource(domainString);
    }

    private String getDate(Element elem) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return outputFormat.format(inputFormat.parse(elem.getElementsByTagName("pubDate").item(0).getTextContent()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getSummary(Element elem) {
        try {
            return Jsoup.parse(elem.getElementsByTagName("description").item(0).getTextContent()).text();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getLink(Element elem) {
        try {
            return elem.getElementsByTagName("link").item(0).getTextContent().trim();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getTitle(Element elem) {
        try {
            return elem.getElementsByTagName("title").item(0).getTextContent();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getGuid(Element elem) {
        try {
            return elem.getElementsByTagName("guid").item(0).getTextContent();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String getSource(String domainString) {
        return domainString
                .replace("www.", "")
                .replace(".com", "");
    }

}
