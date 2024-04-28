package newsaggregator;

import newsaggregator.database.DataAccess;
import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.currency.Coin;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.coin.CoinReader;
import org.bson.Document;


public class Main {
    public static void main(String[] args) {
        DataAccess<Document> db = new MongoDBController();
//        // Articles
//        Scraper<Article> rss = new RSSArticleReader();
//        rss.crawl();
//        db.add("articles", rss.getDataList());
//        db.get("articles", "src/main/resources/rss/data.json");
//        // Posts
//        Scraper<Post> redditReader = new RedditReader();
//        redditReader.crawl();
//        db.add("posts", redditReader.getDataList());
//        db.get("posts", "src/main/resources/reddit/data.json");

        Scraper<Coin> coinReader = new CoinReader();
        coinReader.crawl();
        db.add("coins", coinReader.getDataList());
    }
}
