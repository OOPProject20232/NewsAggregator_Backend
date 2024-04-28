package newsaggregator.webscraping;

import java.util.List;

/**
 * Lớp trừu tượng này dùng để lấy dữ liệu từ các trang web báo.
 * <br> Lớp này sẽ được kế thừa bởi các lớp con.
 * @since 1.0
 * @author Lý Hiển Long
 */
public abstract class Scraper<T> {

    // Attributes

    private List<T> contentList;

    // Methods

    public abstract void crawl();

    public List<T> getContentList() {
        return contentList;
    }

    public void setContentList(List<T> contentList) {
        this.contentList = contentList;
    }
}
