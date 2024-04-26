package newsaggregator.database;

import newsaggregator.model.Model;

import java.util.List;

public interface DataAccess<T> {
    public <D extends Model> T serialize(D item);
    public void get(String collectionName, String filePath);
    public <D extends Model> void add(String collectionName, List<D> list);
    public void createSearchIndex(String collectionName, String indexName);
}