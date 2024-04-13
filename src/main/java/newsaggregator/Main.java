package newsaggregator;

import newsaggregator.database.IPostDataAccess;
import newsaggregator.database.MongoDB;
import newsaggregator.webcrawling.Crawler;
import newsaggregator.webcrawling.rssloader.RSSReader;

public class Main {
    public static void main(String[] args) {
        Crawler rss = new RSSReader();
        rss.crawl();
        IPostDataAccess db = new MongoDB();
        db.importToDatabase(rss.getPostList());
        db.createSearchIndex();
        db.exportDataToJson("src/main/resources/data.json");
    }
}
