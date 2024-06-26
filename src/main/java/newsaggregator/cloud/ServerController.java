package newsaggregator.cloud;

import newsaggregator.database.IDatabase;
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

/**
 * Lớp này là controller cho server ở class Main.java.
 * <br> Có thể thấy lifecycle của crawler như sau:
 * <br> - Crawl dữ liệu từ các nguồn (RSS, Reddit, CoinRanking API)
 * <br> - Lưu dữ liệu vào database
 * <br> - Categorize dữ liệu (với các bộ dữ liệu như articles, posts)
 * <br> - Tạo search index cho bộ dữ liệu (với các bộ dữ liệu như articles, posts)
 * <br> - Trả về kết quả cho client
 *
 * <br> Người dùng cũng có thể lấy dữ liệu về từ database thông qua phương thức get() của MongoDBController.
 *
 * @see MongoDBController
 * @see newsaggregator.Main
 * @see Scraper
 */
public class ServerController {
    private static final IDatabase<Document> DB = new MongoDBController();

    public static String runArticles() {
        // Articles
        Scraper<Article> articles = new RSSArticleReader();
        articles.scrape();
        DB.add("articles", articles.getDataList());
        DB.categorize("articles", articles.getDataList());
//        db.createSearchIndex("articles", "articlesFTS");
        return new JSONObject().put("status", "success").put("message", "Articles added to database.").toString();
    }

    public static String runPosts() {
        // Posts
        Scraper<Post> posts = new RedditReader();
        posts.scrape();
        DB.add("posts", posts.getDataList());
        DB.categorize("posts", posts.getDataList());
//        db.createSearchIndex("posts", "postsFTS");
        return new JSONObject().put("status", "success").put("message", "Posts added to database.").toString();
    }

    public static String runCoins() {
        // Coins
        Scraper<Coin> coins = new CoinReader();
        coins.scrape();
        DB.add("coins", coins.getDataList());
        return new JSONObject().put("status", "success").put("message", "Coins added to database.").toString();
    }
}
