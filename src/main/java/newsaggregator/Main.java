package newsaggregator;

import newsaggregator.database.DataAccess;

import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.content.Article;
import newsaggregator.model.content.Post;
import newsaggregator.webscraping.article.RSSArticleReader;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.post.RedditReader;
import org.bson.Document;


public class Main {
    public static void main(String[] args) {
        DataAccess<Document> db = new MongoDBController();
        // Articles
        Scraper<Article> rss = new RSSArticleReader();
        rss.crawl();
        db.add("articles", rss.getContentList());
        db.get("articles", "src/main/resources/rss/data.json");
        // Posts
        Scraper<Post> redditReader = new RedditReader();
        redditReader.crawl();
        db.add("posts", redditReader.getContentList());
        db.get("posts", "src/main/resources/reddit/data.json");

    }
}
