package newsaggregator.util;

import newsaggregator.model.content.Article;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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
     * Phương thức này dùng để viết dữ liệu từ list các org.bson.Document vào file JSON.
     * @param items List các org.bson.Document.
     * @param filePath Đường dẫn file JSON (dùng để lưu).
     * @see org.bson.Document
     */
    public static void writeDocumentToJson(List<Document> items, String filePath) {
        JSONArray jArray = new JSONArray();
        for (Document item : items) {
            jArray.put(item);
        }
        JSONObject jObject = new JSONObject();
        jObject.put("data", jArray);
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(jObject.toString());
            writer.close();
            System.out.println("\u001B[32m" + "Dữ liêu đã được viết thành công!!!" + "\u001B[0m");
        }
        catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
    }
}