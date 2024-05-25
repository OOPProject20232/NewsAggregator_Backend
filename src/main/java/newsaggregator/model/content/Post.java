package newsaggregator.model.content;

import java.util.List;

public class Post extends Content {

    // Attributes

    private int upVotes;
    private int downVotes;
    private String mediaURL;

    // Constructors

    public Post() {
    }

    public Post(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category, int upVotes, int downVotes, String mediaURL) {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.mediaURL = mediaURL;
    }

    // Methods

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Upvotes: " + upVotes);
        System.out.println("Downvotes: " + downVotes);
        System.out.println("Media URL: " + mediaURL);
        System.out.println("==================================================================");
    }
}
