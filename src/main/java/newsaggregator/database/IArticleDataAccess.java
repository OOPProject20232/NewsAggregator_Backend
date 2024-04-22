package newsaggregator.database;

import newsaggregator.model.Article;
import newsaggregator.model.Model;

import java.util.List;

public interface IArticleDataAccess {
    public void exportDataToJson(String filePath);
    public void importToDatabase(List<? extends Model> list);
    public void createSearchIndex();
}
