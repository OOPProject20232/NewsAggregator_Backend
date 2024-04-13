package newsaggregator.webcrawling;

import newsaggregator.post.Post;

import java.util.List;

/**
 * Lớp trừu tượng này dùng để lấy dữ liệu từ các trang web báo.
 * <br> Lớp này sẽ được kế thừa bởi các lớp con.
 * @since 1.0
 * @author Lý Hiển Long
 */
public abstract class Crawler {

    // Attributes

    private List<Post> postList;

    // Methods

    public abstract void crawl();

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
}
