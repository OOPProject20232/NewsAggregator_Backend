package newsaggregator.database;

import newsaggregator.model.Article;

import java.util.List;

public interface IArticleDataAccess {
    public void exportDataToJson(String filePath);
    public void importToDatabase(List<Article> articleList);
    public void createSearchIndex();
}
