package newsaggregator.model.content;

import java.util.List;

public class Post extends Content {

    // Attributes

    private int upvotes;
    private int downvotes;
    private String mediaURl;

    // Constructors

    public Post() {
    }

    public Post(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category, int upvotes, int downvotes, String mediaURl) {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.mediaURl = mediaURl;
    }

    // Methods

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public String getMediaURL() {
        return mediaURl;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Upvotes: " + upvotes);
        System.out.println("Downvotes: " + downvotes);
        System.out.println("Media URL: " + mediaURl);
        System.out.println("==================================================================");
    }
}
