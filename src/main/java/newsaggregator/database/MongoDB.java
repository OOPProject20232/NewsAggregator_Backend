package newsaggregator.database;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.article.Article;
import newsaggregator.util.JSONWriter;
import okhttp3.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDB implements IArticleDataAccess {

    private final Dotenv dotenv = Dotenv.load();

    /**
     * Phương thức này sẽ kết nối tới database của MongoDB và lấy dữ liệu từ collection `articles`.
     * <br/><br/>
     * Dữ liệu sẽ có định dạng JSON và lưu vào file được chỉ định.
     *
     * @param filePath Địa chỉ lưu file JSON.
     */
    @Override
    public void exportDataToJson(String filePath) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> articlesCollection = db.getCollection("articles");
            FindIterable<Document> documents = articlesCollection.find();
            List<Document> converted_documents = documents.into(new ArrayList<>());
            JSONWriter.writeDocumentToJson(converted_documents, filePath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Phương thức này sẽ kết nối tới database của MongoDB và đẩy dữ liệu từ file JSON lên database thông qua thư viện MongoDB-driver-sync.
     * <br/><br/>
     * Bài viết sẽ được đẩy lên database `WebData`, collection `articles`. Nếu bài viết đã tồn tại trong database, nó sẽ không đẩy lên nữa.
     * <br/><br/>
     * Ngoài ra, phương thức này cũng sẽ sort các bài viết vào các categories khác nhau và lưu vào collection `categories`.
     *
     * @param articleList List các bài viết lấy từ RSSReader.
     */
    @Override
    public void importToDatabase(List<Article> articleList) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> articlesCollection = db.getCollection("articles");
            MongoCollection<Document> categoriesCollection = db.getCollection("categories");
            int count = 0;
            List<Document> documents = new ArrayList<>();
            for (Article article : articleList) {
                try (MongoCursor<Document> cursor = articlesCollection.find(new Document("guid", article.getGuid())).iterator()) {
                    if (!cursor.hasNext()) {
                        Document doc = new Document()
                                .append("guid", article.getGuid())
                                .append("article_link", article.getArticleLink())
                                .append("website_source", article.getWebsiteSource())
                                .append("type", article.getType())
                                .append("article_title", article.getArticleTitle())
                                .append("author", article.getAuthor())
                                .append("creation_date", article.getCreationDate())
                                .append("thumbnail_image", article.getThumbnailImage())
                                .append("article_summary", article.getArticleSummary())
                                .append("article_detailed_content", article.getArticleDetailedContent())
                                .append("category", article.getCategory());
                        documents.add(doc);

                        for (String category : article.getCategory()) {
                            try (MongoCursor<Document> categoriesCursor = categoriesCollection.find(new Document("category", category)).iterator()) {
                                if (!categoriesCursor.hasNext()) {
                                    Document categoryDoc = new Document()
                                            .append("category", category)
                                            .append("articles_guid", Arrays.asList(article.getGuid()));
                                    categoriesCollection.insertOne(categoryDoc);
                                }
                                else {
                                    categoriesCollection.updateOne(new Document("category", category),
                                            new Document("$push", new Document("articles_guid", article.getGuid())));
                                }
                            }
                        }
                        count++;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            try {
                articlesCollection.insertMany(documents);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Đã đẩy " + count + " bài báo lên database...");
        }
    }

    /**
     * Phương thức này sẽ tạo index cho các bài báo trong database nhằm phục vụ cho full-text search.
     * <br/><br/>
     * Index sẽ được tạo ra cho mọi trường thuộc bài báo trong collection `articles`.
     * <br/><br/>
     * Phương pháp tạo index:
     * <br/>- Tạo kết nối HTTP đến MongoDB Atlas với API key được mã hóa bằng phương thức Digest.
     * <br/>- Tạo một request POST với body là thông tin về index cần tạo (tên database, collection, tên index, lựa chọn full-text search).
     * <br/>- Nếu response code là 200, index đã được tạo thành công.
     * <br/>- Nếu response code là 403, đã có index với tên tương tự trong collection => bad request (không đáng lo ngại).
     */
    @Override
    public void createSearchIndex() {
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
                        "{\"collectionName\": \"articles\", " +
                                "\"database\": \"" + dotenv.get("MONGODB_DATABASE_NAME") + "\", " +
                                "\"mappings\": {\"dynamic\": true}, " +
                                "\"name\": \"searchArticles\"}",
                        MediaType.parse("application/json")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("Status code: " + response.code());
            System.out.println("Response body: " + response.body().string());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
