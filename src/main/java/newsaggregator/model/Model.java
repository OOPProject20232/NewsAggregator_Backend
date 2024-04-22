package newsaggregator.model;

import java.util.List;

public class Model {

    // Attributes

    String guid;
    String link;
    String source;
    String type;
    String title;
    String detailedContent;
    String creationDate;
    String author;
    List<String> category;

    // Constructors

    public Model() {
    }

    public Model(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> category) {
        this.guid = guid;
        this.link = link;
        this.source = source;
        this.type = type;
        this.title = title;
        this.detailedContent = detailedContent;
        this.creationDate = creationDate;
        this.author = author;
        this.category = category;
    }

    // Methods

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailedContent() {
        return detailedContent;
    }

    public void setDetailedContent(String detailedContent) {
        this.detailedContent = detailedContent;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }
}
