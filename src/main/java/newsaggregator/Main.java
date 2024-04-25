package newsaggregator;

import newsaggregator.database.DataAccess;

import newsaggregator.database.MongoDB.MongoDBController;
import newsaggregator.model.Article;
import newsaggregator.webscraping.article.RSSArticleReader;
import newsaggregator.webscraping.Scraper;
import org.bson.Document;


public class Main {
    public static void main(String[] args) {
        Scraper<Article> rss = new RSSArticleReader();
        rss.crawl();
        DataAccess<Document> db = new MongoDBController();
        db.add("articles", rss.getContentList());
        db.createSearchIndex("articles", "articlesFTS");
        db.get("articles", "src/main/resources/data.json");
    }
}
