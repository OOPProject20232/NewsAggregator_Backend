package newsaggregator.util;

import newsaggregator.post.Post;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.util.List;

/**
 * Lớp này dùng để viết dữ liệu từ list các bài viết vào file JSON.
 * @see Post
 * @since 1.0
 * @author Lý Hiển Long
 */
public class JSONWriter {

    // Methods

    /**
     * Phương thức này dùng để viết dữ liệu từ list các bài viết vào file JSON.
     * @param postList List các bài viết.
     * @param filePath Đường dẫn file JSON (dùng để lưu).
     * @see Post
     */
    public static void writePostToJson(List<Post> postList, String filePath) {
        JSONArray jArray = new JSONArray();
        for (Post post : postList) {
            JSONObject currentPost = new JSONObject();
            currentPost.put("guid", post.getGuid());
            currentPost.put("article_link", post.getArticleLink());
            currentPost.put("website_source", post.getWebsiteSource());
            currentPost.put("article_type", post.getArticleType());
            currentPost.put("article_title", post.getArticleTitle());
            currentPost.put("author", post.getAuthor());
            currentPost.put("creation_date", post.getCreationDate());
            currentPost.put("thumbnail_image", post.getThumbnailImage());
            currentPost.put("article_summary", post.getArticleSummary());
            currentPost.put("article_detailed_content", post.getArticleDetailedContent());
            currentPost.put("category", post.getCategory());

            jArray.put(currentPost);
        }
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(jArray.toString());
            writer.close();
            System.out.println("Dữ liêu đã được viết thành công!!!");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Phương thức này dùng để viết dữ liệu từ list các BSON Document vào file JSON.
     * @param articles List các BSON Document.
     * @param filePath Đường dẫn file JSON (dùng để lưu).
     * @see org.bson.Document
     */
    public static void writeDocumentToJson(List<Document> articles, String filePath) {
        JSONArray jArray = new JSONArray();
        for (Document article : articles) {
            jArray.put(article);
        }
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(jArray.toString());
            writer.close();
            System.out.println("Dữ liêu đã được viết thành công!!!");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}