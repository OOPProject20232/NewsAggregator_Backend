package newsaggregator.model.content;

import java.util.List;

public class Post extends Content {

    // Attributes

    private int upvotes;
    private int downvotes;

    // Constructors

    public Post() {
    }

    public Post(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category, int upvotes, int downvotes) {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    // Methods

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Upvotes: " + upvotes);
        System.out.println("Downvotes: " + downvotes);
        System.out.println("==================================================================");
    }
}
