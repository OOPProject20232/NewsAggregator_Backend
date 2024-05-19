package newsaggregator.webscraping.article;

import newsaggregator.model.content.Article;
import newsaggregator.webscraping.Scraper;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Lớp RSSArticleReader thực hiện việc đọc file XML từ các nguồn RSS được lưu trữ trong file articleSources.txt
 * và trả về một danh sách các bài báo được lưu trữ trong các file XML này
 */
public class RSSArticleReader extends Scraper<Article> {

    //Methods

    @Override
    public void crawl() {
        System.out.println("\u001B[32m" + "Đang lấy dữ liệu từ các nguồn RSS..." + "\u001B[0m");
        List<Article> articleList = new ArrayList<>();
        try {
            File newsList = new File("src/main/resources/rss/articleSources.txt");
            Scanner newsListScanner = new Scanner(newsList);
            while (newsListScanner.hasNextLine()) {
                String urlString = newsListScanner.nextLine();
                String domainString = URI.create(urlString).getHost();
                int responseCode = RSSSync.getNewUpdate(urlString, "src/main/resources/rss/cache/%s.xml".formatted(domainString));
                if (responseCode == 200) {
                    List<Article> currentArticleList = parseXML("src/main/resources/rss/cache/%s.xml".formatted(domainString), domainString);
                    articleList.addAll(currentArticleList);
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        setDataList(articleList);
        System.out.println("\u001B[32m" + "Đã lấy dữ liệu từ các nguồn RSS..." + "\u001B[0m");
    }

    private List<Article> parseXML(String URIString, String domainString) {
        List<Article> currentArticleList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(URIString);
            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) {
                items = doc.getElementsByTagName("entry");
            }
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
                            getSpecialCategories(elem)
                    );
                    currentArticleList.add(currentArticle);
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return currentArticleList;
    }

    private String getThumbnail(Element elem) {
        try {
            List<String> thumbnailTagList = new ArrayList<>(Arrays.asList("media:thumbnail", "media:content", "enclosure"));
            for (String thumbnailTag : thumbnailTagList) {
                if (elem.getElementsByTagName(thumbnailTag).item(0) != null) {
                    return elem.getElementsByTagName(thumbnailTag).item(0).getAttributes().getNamedItem("url").getTextContent();
                }
            }
            if (elem.getElementsByTagName("content:encoded").item(0) != null) {
                return Jsoup.parse(elem.getElementsByTagName("content:encoded").item(0).getTextContent()).select("img").attr("src");
            } else if (elem.getElementsByTagName("content").item(0) != null) {
                return Jsoup.parse(elem.getElementsByTagName("content").item(0).getTextContent()).select("img").attr("src");
            } else if (elem.getElementsByTagName("description").item(0) != null) {
                return Jsoup.parse(elem.getElementsByTagName("description").item(0).getTextContent()).select("img").attr("src");
            }

        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getDetailedContent(Element elem) {
        List<String> contentList = new ArrayList<>(Arrays.asList("content:encoded", "description", "content", "summary"));
        for (String content : contentList) {
            try {
                Node detailed_contentNode = elem.getElementsByTagName(content).item(0);
                if (detailed_contentNode != null) {
                    if (!detailed_contentNode.getTextContent().isBlank()) {
                        return Jsoup.parse(detailed_contentNode.getTextContent()).text();
                    }
                }
            } catch (Exception e) {
                System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
            }
        }
        return null;
    }

    private List<String> getDefaultCategories(Element elem) {
        ArrayList<String> categories = new ArrayList<>();
        try {
            if (elem.getElementsByTagName("category").getLength() != 0) {
                for (int j = 0; j < elem.getElementsByTagName("category").getLength(); j++) {
                    String category = elem.getElementsByTagName("category").item(j).getTextContent();
                    if (category.isBlank()) {
                        continue;
                    }
                    categories.add(category.toLowerCase());
                }
            } else if (elem.getElementsByTagName("categories").getLength() != 0) {
                String category = elem.getElementsByTagName("categories").item(0).getAttributes().getNamedItem("label").getTextContent();
                categories.add(category.toLowerCase());
            }
            return categories;
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    @Deprecated
    private List<String> getMLCategories(Element elem) {
        String content = getDetailedContent(elem);
        if (content == null) {
            throw new IllegalStateException("Contents must not be empty!!!");
        }
        Set<String> categories = new HashSet<>();
        try {
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(content);
            List<String> models = Arrays.asList("en-ner-person.bin", "en-ner-organization.bin");
            for (String model : models) {
                // NER
                InputStream inputStreamNameFinder = getClass().getResourceAsStream("/mlmodels/%s".formatted(model));
                assert inputStreamNameFinder != null;
                TokenNameFinderModel NERmodel = new TokenNameFinderModel(
                        inputStreamNameFinder);
                NameFinderME nameFinderME = new NameFinderME(NERmodel);
                List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
                var stopWords = Files.readAllLines(Paths.get("src/main/resources/mlmodels/stopwords.txt"));
                if (!spans.isEmpty()) {
                    for (Span span : spans) {
                        String token = tokens[span.getStart()].toLowerCase();
                        if (!stopWords.contains(token) && !isCommonPunctuation(token) && token.length() > 1) {
                            categories.add(token);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return categories.isEmpty()? List.of("general"): new ArrayList<>(categories);
    }

    private List<String> getSpecialCategories(Element elem) {
        String content = getTitle(elem) + " " + getDetailedContent(elem);
        List<String> categories = new ArrayList<>();
        content = content.toLowerCase();
        try {
            var specialCategories = Files.readAllLines(Paths.get("src/main/resources/mlmodels/special-categories.txt"))
                    .stream()
                    .map(String::toLowerCase)
                    .map(String::trim).toList();
            String[] contentWords = content.split(" ");
            for (int i = 0; i < contentWords.length; i++) {
                contentWords[i] = contentWords[i].replaceAll("\\p{Punct}", "");
                if (specialCategories.contains(contentWords[i]) && !categories.contains(contentWords[i])) {
                    categories.add(contentWords[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return categories.isEmpty()? List.of("general") : categories;
    }

    private boolean isCommonPunctuation(String token) {
        return token.equals("&") || token.equals("-") || token.equals("/") || token.equals("\"")
                || token.equals(".") || token.equals(",") || token.equals(" ") || token.equals("_");
    }

    private String getAuthor(String domainString, Element elem) {
        try {
            if (elem.getElementsByTagName("dc:creator").item(0) != null) {
                return elem.getElementsByTagName("dc:creator").item(0).getTextContent();
            }
            else if (elem.getElementsByTagName("author").item(0).getTextContent() != null) {
                return elem.getElementsByTagName("author").item(0).getTextContent();
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return getSource(domainString);
    }

    private String getDate(Element elem) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Node dateNode = elem.getElementsByTagName("pubDate").item(0);
            if (dateNode == null) {
                dateNode = elem.getElementsByTagName("published").item(0);
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                return outputFormat.format(inputFormat2.parse(dateNode.getTextContent()));
            }
            SimpleDateFormat inputFormat1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            return outputFormat.format(inputFormat1.parse(dateNode.getTextContent()));
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getSummary(Element elem) {
        try {
            Node summary = elem.getElementsByTagName("description").item(0);
            if (summary == null) {
                summary = elem.getElementsByTagName("summary").item(0);
            }
            return Jsoup.parse(summary.getTextContent()).text();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getLink(Element elem) {
        try {
            return elem.getElementsByTagName("link").item(0).getTextContent().trim();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getTitle(Element elem) {
        try {
            return elem.getElementsByTagName("title").item(0).getTextContent().trim();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getGuid(Element elem) {
        try {
            Node guid = elem.getElementsByTagName("guid").item(0);
            if (guid == null) {
                guid = elem.getElementsByTagName("id").item(0);
            }
            return guid.getTextContent().trim();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }

    private String getSource(String domainString) {
        return domainString
                .replace("www.", "")
                .replace(".com", "");
    }

}
