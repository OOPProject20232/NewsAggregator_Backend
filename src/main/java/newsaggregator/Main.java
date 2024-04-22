package newsaggregator;

import newsaggregator.database.IArticleDataAccess;
import newsaggregator.database.MongoDB;
import newsaggregator.webscraping.Scraper;
import newsaggregator.webscraping.article.RSSReader;

public class Main {
    public static void main(String[] args) {
        Scraper rss = new RSSReader();
        rss.crawl();
        IArticleDataAccess db = new MongoDB();
        db.importToDatabase(rss.getContentList());
        db.createSearchIndex();
        db.exportDataToJson("src/main/resources/data.json");
    }
}
