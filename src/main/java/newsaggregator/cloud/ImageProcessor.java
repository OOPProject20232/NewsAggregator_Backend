package newsaggregator.cloud;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ImageProcessor {
    public static final Dotenv dotenv = Dotenv.load();
    public static final MongoDatabase db = MongoClients
            .create(dotenv.get("MONGODB_CONNECTION_STRING"))
            .getDatabase(dotenv.get("MONGODB_DATABASE_NAME"));
    public static final GridFSBucket gridFS = GridFSBuckets.create(db, "images");

    public static void init(String url) throws Exception {
        try (InputStream inputStream = downloadFile(url)) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            GridFSUploadOptions opts = new GridFSUploadOptions()
                    .chunkSizeBytes(1048576)
                    .metadata(new Document("type", "image"));
            Object fileID = gridFS.uploadFromStream(fileName, inputStream, opts);
        }
    }

    public static InputStream downloadFile(String url) {
        try {
            URL fileURL = URI.create(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) fileURL.openConnection();
            connection.connect();
            return connection.getInputStream();
        } catch (Exception e) {
            System.out.println("\u001B[31m" + e.getMessage() + "\u001B[0m");
        }
        return null;
    }
}
