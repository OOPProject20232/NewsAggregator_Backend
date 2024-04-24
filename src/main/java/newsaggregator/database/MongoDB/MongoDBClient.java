package newsaggregator.database.MongoDB;

import newsaggregator.database.DataAccess;
import newsaggregator.model.Model;
import org.bson.Document;

import java.util.List;

public interface MongoDBClient extends DataAccess<Document> {
    public <D extends Model> Document serialize(D item);
    public void get(String collectionName, String filePath);
    public void add(String collectionName, List<? extends Model> list);
    public void createSearchIndex(String collectionName, String indexName);
}
