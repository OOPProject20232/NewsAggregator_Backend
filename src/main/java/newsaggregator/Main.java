package newsaggregator;

import newsaggregator.database.DataAccess;

import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.Article;
import newsaggregator.model.Post;
import newsaggregator.webscraping.article.RSSArticleReader;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.post.RedditReader;
import org.bson.Document;


public class Main {
    public static void main(String[] args) {
//        Scraper<Article> rss = new RSSArticleReader();
//        rss.crawl();
//        db.add("articles", rss.getContentList());
//        db.createSearchIndex("articles", "articlesFTS");

        DataAccess<Document> db = new MongoDBController();
        Scraper<Post> redditReader = new RedditReader();
        redditReader.crawl();
        db.add("posts", redditReader.getContentList());
        db.createSearchIndex("posts", "postsFTS");

    }
}
