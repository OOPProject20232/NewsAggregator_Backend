package newsaggregator.model;

import java.util.List;

public class Post extends Model {

    // Attributes

    private int upvotes;
    private int downvotes;
    private int views;
    private List<String> comments;

    // Constructors

    public Post() {
    }

    public Post(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category, int upvotes, int downvotes, List<String> comments) {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments = comments;
    }

    // Methods

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
