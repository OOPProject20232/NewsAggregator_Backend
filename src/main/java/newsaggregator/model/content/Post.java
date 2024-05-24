package newsaggregator.model.content;

import java.util.List;

public class Post extends Content {

    // Attributes

    private int upvotes;
    private int downvotes;
    private String mediaURL;

    // Constructors

    public Post() {
    }

    public Post(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category, int upvotes, int downvotes, String mediaURL) {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.mediaURL = mediaURL;
    }

    // Methods

    public int getUpVotes() {
        return upvotes;
    }

    public int getDownVotes() {
        return downvotes;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Upvotes: " + upvotes);
        System.out.println("Downvotes: " + downvotes);
        System.out.println("Media URL: " + mediaURL);
        System.out.println("==================================================================");
    }
}
