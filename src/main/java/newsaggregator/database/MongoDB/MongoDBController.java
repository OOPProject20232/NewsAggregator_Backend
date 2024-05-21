package newsaggregator.database.MongoDB;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.cloud.ServerController;
import newsaggregator.database.Database;
import newsaggregator.model.BaseModel;
import newsaggregator.model.content.Article;
import newsaggregator.model.content.Content;
import newsaggregator.model.content.Post;
import newsaggregator.model.crypto.Coin;
import newsaggregator.jsonwriter.JSONWriter;
import okhttp3.*;
import org.bson.Document;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lớp này thực hiện các thao tác với MongoDB, có vòng đời (life cycle) như sau:
 * <br>- serialize (biến dữ liệu vừa tìm được về dạng giúp MongoDB hiểu được).
 * <br>- add (đẩy dữ liệu lên MongoDB).
 * <br>- categorize (phân loại dữ liệu vào các categories).
 * <br>- get (tùy chọn; lấy dữ liệu từ MongoDB và lưu vào file JSON).
 * <br>Trong đó:
 * <br>- serialize, add, categorize sẽ được gọi từ lớp App.
 * <br>- get chỉ có tác dụng nếu lập trình viên clone repo và call tới phương thức này.
 * @see ServerController
 */
public class MongoDBController implements MongoDBClient{

    private final Dotenv dotenv = Dotenv.load();

    /**
     * Phương thức này dùng để chuyển đổi một đối tượng kế thừa từ lớp BaseModel thành định dạng org.bson.Document để lưu vào MongoDB.
     * @see BaseModel
     * @see Document
     *
     * @param item Đối tượng cần chuyển đổi.
     * @param <D> Kiểu dữ liệu của đối tượng. Ví dụ: Article, Post, Coin.
     * @return item dưới dạng org.bson.Document.
     */
    @Override
    public <D extends BaseModel> Document serialize(D item) {
        if (item instanceof Article) {
            return new Document()
                    .append("guid", item.getGuid())
                    .append("article_link", ((Content) item).getLink())
                    .append("website_source", ((Content) item).getSource())
                    .append("type_", item.getType())
                    .append("article_title", ((Content) item).getTitle())
                    .append("author", ((Content) item).getAuthor())
                    .append("creation_date", ((Content) item).getCreationDate())
                    .append("thumbnail_image", ((Article) item).getThumbnailImage())
                    .append("article_summary", ((Article) item).getSummary())
                    .append("article_detailed_content", ((Content) item).getDetailedContent())
                    .append("categories", ((Content) item).getCategories());
        }
        else if (item instanceof Post) {
            return new Document()
                    .append("guid", item.getGuid())
                    .append("post_link", ((Content) item).getLink())
                    .append("website_source", ((Content) item).getSource())
                    .append("type_", item.getType())
                    .append("post_title", ((Content) item).getTitle())
                    .append("author", ((Content) item).getAuthor())
                    .append("creation_date", ((Content) item).getCreationDate())
                    .append("post_content", ((Content) item).getDetailedContent())
                    .append("categories", ((Content) item).getCategories())
                    .append("up_votes", ((Post) item).getUpvotes())
                    .append("down_votes", ((Post) item).getDownvotes())
                    .append("media_url", ((Post) item).getMediaURL());
        }
        else if (item instanceof Coin) {
            Document doc = new Document()
                    .append("guid", item.getGuid())
                    .append("type_", item.getType())
                    .append("symbol", ((Coin) item).getSymbol())
                    .append("name", ((Coin) item).getName())
                    .append("thumbnail_image", ((Coin) item).getThumbnailImage())
                    .append("market_cap", ((Coin) item).getMarketCap())
                    .append("rank", ((Coin) item).getRank())
                    .append("btc_price", ((Coin) item).getBtcPrice());
            Document priceDocument = new Document();
            for (AbstractMap.SimpleEntry<String, String> entry : ((Coin) item).getPrices()) {
                priceDocument.append(entry.getKey(), entry.getValue());
            }
            doc.append("prices", priceDocument);
            return doc;
        }
        else {
            throw new IllegalArgumentException("\u001B[31m" + "Dữ liệu không hợp lệ!" + "\u001B[0m");
        }
    }

    /**
     * Phương thức này lấy hết dữ liệu từ MongoDB và lưu vào một file JSON
     * <br>(LƯU Ý: DỮ LIỆU ĐƯỢC LƯU Ở DẠNG JSON ARRAY).
     * @param collectionName Tên collection trong MongoDB.
     * @param filePath Địa chỉ lưu file JSON.
     *
     * @see JSONWriter
     */
    @Override
    public void get(String collectionName,String filePath) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> collection = db.getCollection(collectionName);
            FindIterable<Document> documents = collection.find();
            List<Document> converted_documents = documents.into(new ArrayList<>());
            JSONWriter.writeDocumentToJson(converted_documents, filePath);
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }

    /**
     * Phương thức này push dữ liệu tìm được lên MongoDB.
     * <br> Nếu dữ liệu là Coin, sẽ xóa hết dữ liệu cũ trong collection trước khi đẩy dữ liệu mới lên.
     * <br> Nếu dữ liệu là Article hoặc Post, sẽ bỏ qua các bài viết đã tồn tại trong collection => tránh trùng lặp.
     *
     * @param collectionName Tên collection trong MongoDB.
     * @param contentList List các bài báo/bài viết/coin.
     */
    @Override
    public <D extends BaseModel> void add(String collectionName, List<D> contentList) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> collection = db.getCollection(collectionName);
            if (collectionName.equals("coins")) {
                collection.drop();
            }
            int count = 0;
            List<Document> documents = new ArrayList<>();
            Set<String> existingGuids = collection.distinct("guid", String.class).into(new HashSet<>());
            for (BaseModel item : contentList) {
                if (!existingGuids.contains(item.getGuid())) {
                    documents.add(serialize(item));
                    existingGuids.add(item.getGuid());
                    count++;
                }
            }
            try {
                collection.insertMany(documents);
            } catch (Exception e) {
                System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
            }
            System.out.println("\u001B[32m" + "Đã đẩy " + count + " bài viết lên database..." + "\u001B[0m");
        }
    }


    /**
     * Phương thức này sẽ phân loại các lớp kế thừa từ lớp Content vào các categories.
     * <br> Ví dụ: Một bài viết có categories là ["Bitcoin", "Ethereum"], sẽ được phân vào 2 categories "Bitcoin" và "Ethereum".
     * @param collectionName Tên collection trong MongoDB.
     * @param list List các bài báo/bài viết.
     * @param <D> Kiểu dữ liệu của đối tượng. Ví dụ: Article, Post.
     */
    @Override
    public <D extends Content> void categorize(String collectionName, List<D> list) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> categoriesCollection = db.getCollection(collectionName + ".categories");
            Map<String, List<String>> categoriesUpdates = new HashMap<>();
            for (Content item : list) {
                for (String category : item.getCategories()) {
                    if (category == null) {
                        continue;
                    }
                    List<String> guids = categoriesUpdates.getOrDefault(category, new ArrayList<>());
                    guids.add(item.getGuid());
                    categoriesUpdates.put(category, guids);
                }
            }
            for (Map.Entry<String, List<String>> entry : categoriesUpdates.entrySet()) {
                try (MongoCursor<Document> categoryCursor = categoriesCollection.find(new Document("category", entry.getKey())).iterator()) {
                    if (!categoryCursor.hasNext()) {
                        Document categoryDocument = new Document()
                                .append("category", entry.getKey())
                                .append(collectionName + "_guid", entry.getValue());
                        categoriesCollection.insertOne(categoryDocument);
                    } else {
                        categoriesCollection.updateOne(new Document("category", entry.getKey()),
                                new Document("$addToSet", new Document(collectionName + "_guid", new Document("$each", entry.getValue()))));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        System.out.println("\u001B[32m" + "Đã phân loại bài viết vào các categories..." + "\u001B[0m");
    }

    /**
     * Phương thức này sẽ tạo index cho các bài báo trong database nhằm phục vụ cho full-text search.
     * <br/>
     * Index sẽ được tạo ra cho mọi trường thuộc document trong collection mà người dùng chọn (Nên là Article hay Post).
     * <br/>
     * Phương pháp tạo index:
     * <br/>- Tạo kết nối HTTP đến MongoDB Atlas với API key được mã hóa bằng phương thức Digest.
     * <br/>- Tạo một request POST với body là thông tin về index cần tạo (tên database, collection, tên index, lựa chọn full-text search).
     * <br/>- Nếu response code là 200, index đã được tạo thành công.
     * <br/>- Nếu response code là 403, đã có index với tên tương tự trong collection => bad request (không đáng lo ngại).
     *
     * @param collectionName Tên collection trong MongoDB.
     * @param indexName Tên index cần tạo.
     */
    @Override
    public void createSearchIndex(String collectionName, String indexName) {
        final DigestAuthenticator authenticator =
                new DigestAuthenticator(
                        new Credentials(dotenv.get("MONGODB_PUBLIC_API_KEY"), dotenv.get("MONGODB_PRIVATE_API_KEY")));
        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url("https://cloud.mongodb.com/api/atlas/v1.0/groups/" + dotenv.get("MONGO_DB_GROUP_ID") + "/clusters/"
                        + dotenv.get("MONGODB_CLUSTER_NAME") + "/fts/indexes?pretty=true")
                .post(RequestBody.create(
                        "{\"collectionName\": \"" + collectionName + "\", " +
                                "\"database\": \"" + dotenv.get("MONGODB_DATABASE_NAME") + "\", " +
                                "\"mappings\": {\"dynamic\": true}, " +
                                "\"name\": \""+ indexName + "\"}",
                        MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status code: " + response.code());
            System.out.println("Response body: " + response.body().string());
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }

    /**
     * Phương thức này sẽ tạo thông tin của các nguồn tin như: tên, logo url, tên tham chiếu để phụ vụ tham chiếu giữa các collection trong MongoDB.
     * <br> Lưu ý: Chỉ cần chạy 1 lần để tạo thông tin của các nhà xuất bản, tạo 2 lần sẽ gây ra lỗi. Nếu gặp phải lỗi, hãy drop collection.
     * <br> Do MongoDB là NoSQL database nên cần phải dùng phương thức này để tham chiếu, với SQL database, không cần phương thức này nên không có trong DataAccess interface.
     * @see Database
     */
    public void generatePublisherInfo() {
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
                        org.w3c.dom.Document document = documentBuilder.parse(inputStream);
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
            get("articles.publishers", "src/main/resources/rss/publishers.json");
        } catch (FileNotFoundException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
