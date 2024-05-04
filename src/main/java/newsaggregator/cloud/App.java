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
import org.json.JSONObject;

public class App {
    public static final DataAccess<Document> db = new MongoDBController();

    public static String runArticles() {
        // Articles
        Scraper<Article> articles = new RSSArticleReader();
        articles.crawl();
        db.add("articles", articles.getDataList());
        db.createSearchIndex("articles", "articlesFTS");
        return new JSONObject().put("status", "success").put("message", "Articles added to database.").toString();
    }

    public static String runPosts() {
        // Posts
        Scraper<Post> posts = new RedditReader();
        posts.crawl();
        db.add("posts", posts.getDataList());
        db.createSearchIndex("posts", "postsFTS");
        return new JSONObject().put("status", "success").put("message", "Posts added to database.").toString();
    }

    public static String runCoins() {
        // Coins
        Scraper<Coin> coins = new CoinReader();
        coins.crawl();
        db.add("coins", coins.getDataList());
        return new JSONObject().put("status", "success").put("message", "Coins added to database.").toString();
    }
}
