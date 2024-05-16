package newsaggregator.database.MongoDB;

import newsaggregator.database.DataAccess;
import newsaggregator.model.BaseModel;
import newsaggregator.model.content.Content;
import org.bson.Document;

import java.util.List;

public interface MongoDBClient extends DataAccess<Document> {
    public <D extends BaseModel> Document serialize(D item);
    public void get(String collectionName, String filePath);
    public <D extends BaseModel> void add(String collectionName, List<D> list);
    public void createSearchIndex(String collectionName, String indexName);
    public <D extends Content> void categorize(String collectionName, List<D> list);
}
