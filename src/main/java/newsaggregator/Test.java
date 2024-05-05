package newsaggregator;

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

public class Test {
    public static final DataAccess<Document> db = new MongoDBController();

    public static void main(String[] args) {
        // Articles
//        Scraper<Article> articles = new RSSArticleReader();
//        articles.crawl();
//        for (Article article : articles.getDataList()) {
//            System.out.println(article.getCategories());
//        }
//        db.add("articles", articles.getDataList());
//        db.categorize("articles", articles.getDataList());
//        db.createSearchIndex("articles", "articlesFTS");
//        // Posts
        Scraper<Post> posts = new RedditReader();
        posts.crawl();
        for (Post post : posts.getDataList()) {
            post.display();
        }
//        db.add("posts", posts.getDataList());
//        db.categorize("posts", posts.getDataList());
//        db.createSearchIndex("posts", "postsFTS");
//        // Coins
//        Scraper<Coin> coins = new CoinReader();
//        coins.crawl();
//        db.add("coins", coins.getDataList());
    }
}
