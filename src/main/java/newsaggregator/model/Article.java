package newsaggregator.model;

import org.bson.Document;

import java.util.List;

/**
 * Lớp này dùng để biểu diễn một bài viết.
 * @since 1.0
 * @author Lý Hiển Long
 */
public class Article extends Model {

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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getSummary() {
        return summary;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void display() {
        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println();
        System.out.println("GUID: " + guid);
        System.out.println("Article link: " + link);
        System.out.println("Website source: " + source);
        System.out.println("Type: " + type);
        System.out.println("Article title: " + title);
        System.out.println("Article Summary: " + summary);
        System.out.println();
        System.out.println("Detailed article content: " + detailedContent);
        System.out.println();
        System.out.println("Creation date: " + creationDate);
        System.out.println("Author's name: " + author);
        System.out.println("Thumbnail image: " + thumbnailImage);
        System.out.println("Categories: " + categories);
        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println();
    }


    public Document toMongoDocument() {
        return new Document()
                .append("guid", guid)
                .append("article_link", link)
                .append("website_source", source)
                .append("type_", type)
                .append("article_title", title)
                .append("author", author)
                .append("creation_date", creationDate)
                .append("thumbnail_image", thumbnailImage)
                .append("article_summary", categories)
                .append("article_detailed_content", detailedContent)
                .append("categories", categories);
    }
}
