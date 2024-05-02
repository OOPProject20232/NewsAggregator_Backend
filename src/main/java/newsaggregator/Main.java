package newsaggregator;

import newsaggregator.database.DataAccess;
import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.content.Article;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.article.RSSArticleReader;
import org.bson.Document;


public class Main {
    public static void main(String[] args) {
//        DataAccess<Document> db = new MongoDBController();
        // Articles
        Scraper<Article> rss = new RSSArticleReader();
        rss.crawl();
        for (Article article : rss.getDataList()) {
            article.display();
        }
//        db.add("articles", rss.getDataList());
//        db.get("articles", "src/main/resources/rss/data.json");
//        // Posts
//        Scraper<Post> redditReader = new RedditReader();
//        redditReader.crawl();
//        db.add("posts", redditReader.getDataList());
//        db.get("posts", "src/main/resources/reddit/data.json");
//        // Coins
//        Scraper<Coin> coinReader = new CoinReader();
//        coinReader.crawl();
//        db.add("coins", coinReader.getDataList());
//        db.get("coins", "src/main/resources/crypto/data.json");
    }
}
