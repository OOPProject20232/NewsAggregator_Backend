package newsaggregator.cloud;

import newsaggregator.database.DataAccess;
import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.content.Article;
import newsaggregator.model.content.Post;
import newsaggregator.model.crypto.Coin;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.article.RSSArticleReader;
import newsaggregator.webscraping.coin.CoinReader;
import newsaggregator.webscraping.post.RedditReader;
import org.bson.Document;

public class App {
    public static final DataAccess<Document> db = new MongoDBController();

    public static String runArticles() {
        // Articles
        Scraper<Article> articles = new RSSArticleReader();
        articles.crawl();
        db.add("articles", articles.getDataList());
        db.createSearchIndex("articles", "articlesFTS");
        return "Articles added to database.";
    }

    public static String runPosts() {
        // Posts
        Scraper<Post> posts = new RedditReader();
        posts.crawl();
        db.add("posts", posts.getDataList());
        db.createSearchIndex("posts", "postsFTS");
        return "Posts added to database.";
    }

    public static String runCoins() {
        // Coins
        Scraper<Coin> coins = new CoinReader();
        coins.crawl();
        db.add("coins", coins.getDataList());
        return "Coins added to database.";
    }
}
