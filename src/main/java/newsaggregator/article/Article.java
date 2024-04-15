package newsaggregator.article;

import java.util.List;

/**
 * Lớp này dùng để biểu diễn một bài viết.
 * @since 1.0
 * @author Lý Hiển Long
 */
public class Article {

    // Attributes

    private String guid;
    private String articleLink;
    private String websiteSource;
    private String type;
    private String articleTitle;
    private String articleSummary;
    private String articleDetailedContent;
    private String creationDate;
    private String author;
    private String thumbnailImage;
    private List<String> category;

    // Constructors

    public Article() {
    }

    public Article(String guid, String articleLink, String websiteSource, String type, String articleTitle,
                   String articleSummary, String articleDetailedContent, String creationDate,
                   String author, String thumbnailImage, List<String> category) {
    this.guid = guid;
    this.articleLink = articleLink;
    this.websiteSource = websiteSource;
    this.type = type;
    this.articleTitle = articleTitle;
    this.articleSummary = articleSummary;
    this.articleDetailedContent = articleDetailedContent;
    this.creationDate = creationDate;
    this.author = author;
    this.thumbnailImage = thumbnailImage;
    this.category = category;
    }

    // Methods

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }

    public void setWebsiteSource(String websiteSource) {
        this.websiteSource = websiteSource;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public void setArticleSummary(String articleSummary) {
        this.articleSummary = articleSummary;
    }

    public void setArticleDetailedContent(String articleDetailedContent) {
        this.articleDetailedContent = articleDetailedContent;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getGuid() {
        return guid;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public String getWebsiteSource() {
        return websiteSource;
    }

    public String getType() {
        return type;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleSummary() {
        return articleSummary;
    }

    public String getArticleDetailedContent() {
        return articleDetailedContent;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public List<String> getCategory() {
        return category;
    }

    public void display() {
        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println();
        System.out.println("GUID: " + guid);
        System.out.println("Article link: " + articleLink);
        System.out.println("Website source: " + websiteSource);
        System.out.println("Type: " + type);
        System.out.println("Article title: " + articleTitle);
        System.out.println("Article Summary: " + articleSummary);
        System.out.println();
        System.out.println("Detailed article content: " + articleDetailedContent);
        System.out.println();
        System.out.println("Creation date: " + creationDate);
        System.out.println("Author's name: " + author);
        System.out.println("Thumbnail image: " + thumbnailImage);
        System.out.println("Categories: " + category);
        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println();
    }
}
