package newsaggregator.webscraping;

import newsaggregator.article.Article;

import java.util.List;

/**
 * Lớp trừu tượng này dùng để lấy dữ liệu từ các trang web báo.
 * <br> Lớp này sẽ được kế thừa bởi các lớp con.
 * @since 1.0
 * @author Lý Hiển Long
 */
public abstract class Scraper {

    // Attributes

    private List<Article> articleList;

    // Methods

    public abstract void crawl();

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }
}
