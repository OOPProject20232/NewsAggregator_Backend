package newsaggregator.database.MongoDB;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import newsaggregator.model.BaseModel;
import newsaggregator.model.content.Article;
import newsaggregator.model.content.Content;
import newsaggregator.model.content.Post;
import newsaggregator.model.currency.Coin;
import newsaggregator.util.JSONWriter;
import okhttp3.*;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDBController implements MongoDBClient{

    private final Dotenv dotenv = Dotenv.load();

    @Override
    public <D extends BaseModel> Document serialize(D item) {
        if (item instanceof Article) {
            return new Document()
                    .append("guid", item.getGuid())
                    .append("article_link", ((Content) item).getLink())
                    .append("website_source", ((Content) item).getSource())
                    .append("type_", ((Content) item).getType())
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
                    .append("type_", ((Content) item).getType())
                    .append("post_title", ((Content) item).getTitle())
                    .append("author", ((Content) item).getAuthor())
                    .append("creation_date", ((Content) item).getCreationDate())
                    .append("post_content", ((Content) item).getDetailedContent())
                    .append("categories", ((Content) item).getCategories())
                    .append("up_votes", ((Post) item).getUpvotes())
                    .append("down_votes", ((Post) item).getDownvotes());
        }
        else if (item instanceof Coin) {
            return null;
        }
        else {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ!");
        }
    }

    /**
     * Phương thức này sẽ kết nối tới database của MongoDB và lấy dữ liệu từ collection `articles`.
     * <br/><br/>
     * Dữ liệu sẽ có định dạng JSON và lưu vào file được chỉ định.
     *
     * @param filePath Địa chỉ lưu file JSON.
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
     * @param contentList List các bài báo/bài viết.
     */
    @Override
    public <D extends BaseModel> void add(String collectionName, List<D> contentList) {
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGODB_CONNECTION_STRING"))) {
            MongoDatabase db = mongoClient.getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
            MongoCollection<Document> contentCollection = db.getCollection(collectionName);
            MongoCollection<Document> categoriesCollection = db.getCollection(collectionName + ".categories");
            int count = 0;
            List<Document> documents = new ArrayList<>();
            Set<String> existingGuids = contentCollection.distinct("guid", String.class).into(new HashSet<>());
            for (BaseModel item : contentList) {
                if (!existingGuids.contains(item.getGuid())) {
                    documents.add(serialize(item));
                    existingGuids.add(item.getGuid());
                    if (item instanceof  Content) {
                        for (String category : ((Content) item).getCategories()) {
                            if (category == null) {
                                continue;
                            }
                            try (MongoCursor<Document> categoryCursor = categoriesCollection.find(new Document("category", category)).iterator()) {
                                if (!categoryCursor.hasNext()) {
                                    Document categoryDocument = new Document()
                                            .append("category", category)
                                            .append(collectionName + "_guid", Arrays.asList(item.getGuid()));
                                    categoriesCollection.insertOne(categoryDocument);
                                } else {
                                    categoriesCollection.updateOne(new Document("category", category),
                                            new Document("$push", new Document(collectionName + "_guid", item.getGuid())));
                                }
                            }
                        }
                    }
                    count++;
                }
            }
            try {
                contentCollection.insertMany(documents);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Đã đẩy " + count + " bài viết lên database...");
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
            System.out.println(e.getMessage());
        }
    }
}
