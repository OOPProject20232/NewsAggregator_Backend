package newsaggregator.database;

import newsaggregator.post.Post;

import java.util.List;

public interface IPostDataAccess {
    public void exportDataToJson(String filePath);
    public void importToDatabase(List<Post> postList);
    public void createSearchIndex();
}
