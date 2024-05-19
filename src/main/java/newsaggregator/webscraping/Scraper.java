package newsaggregator.webscraping;

import java.util.List;

/**
 * Lớp trừu tượng này dùng để lấy dữ liệu từ các trang web báo.
 * <br> Lớp này sẽ được kế thừa bởi các lớp con.
 * @author Lý Hiển Long
 */
public abstract class Scraper<T> {

    // Attributes

    private List<T> dataList;

    // Methods

    public abstract void crawl();

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
