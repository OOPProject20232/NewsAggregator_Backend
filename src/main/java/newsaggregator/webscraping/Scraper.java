package newsaggregator.webscraping;

import newsaggregator.model.Article;
import newsaggregator.model.Model;

import java.util.List;

/**
 * Lớp trừu tượng này dùng để lấy dữ liệu từ các trang web báo.
 * <br> Lớp này sẽ được kế thừa bởi các lớp con.
 * @since 1.0
 * @author Lý Hiển Long
 */
public abstract class Scraper {

    // Attributes

    private List<? extends Model> contentList;

    // Methods

    public abstract void crawl();

    public List<? extends Model> getContentList() {
        return contentList;
    }

    public void setContentList(List<? extends Model> contentList) {
        this.contentList = contentList;
    }
}
