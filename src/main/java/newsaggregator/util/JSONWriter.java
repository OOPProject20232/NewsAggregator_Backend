package newsaggregator.util;

import newsaggregator.model.content.Article;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.util.List;

/**
 * Lớp này dùng để viết dữ liệu từ list các bài viết vào file JSON.
 * @see Article
 * @since 1.0
 * @author Lý Hiển Long
 */
public class JSONWriter {

    // Methods

    /**
     * Phương thức này dùng để viết dữ liệu từ list các bài viết vào file JSON.
     * @param articleList List các bài viết.
     * @param filePath Đường dẫn file JSON (dùng để lưu).
     * @see Article
     */
    public static void writePostToJson(List<Article> articleList, String filePath) {
        JSONArray jArray = new JSONArray();
        for (Article article : articleList) {
            JSONObject currentPost = new JSONObject();
            currentPost.put("guid", article.getGuid());
            currentPost.put("article_link", article.getLink());
            currentPost.put("website_source", article.getSource());
            currentPost.put("type_", article.getType());
            currentPost.put("article_title", article.getTitle());
            currentPost.put("author", article.getAuthor());
            currentPost.put("creation_date", article.getCreationDate());
            currentPost.put("thumbnail_image", article.getThumbnailImage());
            currentPost.put("article_summary", article.getSummary());
            currentPost.put("article_detailed_content", article.getDetailedContent());
            currentPost.put("category", article.getCategories());

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
     * @param items List các BSON Document.
     * @param filePath Đường dẫn file JSON (dùng để lưu).
     * @see org.bson.Document
     */
    public static void writeDocumentToJson(List<Document> items, String filePath) {
        JSONArray jArray = new JSONArray();
        for (Document item : items) {
            jArray.put(item);
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