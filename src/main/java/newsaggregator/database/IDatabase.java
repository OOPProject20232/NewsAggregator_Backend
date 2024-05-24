package newsaggregator.database;

import newsaggregator.model.BaseModel;
import newsaggregator.model.content.Content;

import java.util.List;

public interface IDatabase<T> {
    public <D extends BaseModel> T serialize(D item);
    public void get(String collectionName, String filePath);
    public <D extends BaseModel> void add(String collectionName, List<D> list);
    public void createSearchIndex(String collectionName, String indexName);
    public <D extends Content> void categorize(String collectionName, List<D> list);
}
