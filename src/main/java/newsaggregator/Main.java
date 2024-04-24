package newsaggregator;

import newsaggregator.database.MongoDB.MongoDBClient;
import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.Article;
import newsaggregator.webscraping.RSSReader;
import newsaggregator.webscraping.Scraper;


public class Main {
    public static void main(String[] args) {
        Scraper rss = new RSSReader();
        rss.crawl();
        MongoDBClient db = new MongoDBController();
        db.add("articles", rss.getArticleList());
        db.createSearchIndex("articles", "articlesFTS2");
    }
}
