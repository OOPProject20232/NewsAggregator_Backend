package newsaggregator.model.content;

import java.util.List;

/**
 * Lớp này dùng để biểu diễn một bài viết.
 * @since 1.0
 * @author Lý Hiển Long
 */
public class Article extends Content {

    // Attributes

    private String summary;
    private String thumbnailImage;

    // Constructors

    public Article() {
    }

    public Article(String guid, String link, String source, String type, String title,
                   String summary, String detailedContent, String creationDate,
                   String author, String thumbnailImage, List<String> category)
    {
        super(guid, link, source, type, title, detailedContent, creationDate, author, category);
        this.summary = summary;
        this.thumbnailImage = thumbnailImage;
    }

    // Methods

    public String getSummary() {
        return summary;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    @Override
    public void display() {
        System.out.println("==================================================================");
        super.display();
        System.out.println("Summary: " + summary);
        System.out.println("Thumbnail image: " + thumbnailImage);
        System.out.println("==================================================================");
    }
}
