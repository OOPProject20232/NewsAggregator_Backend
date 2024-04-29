package newsaggregator.model.content;

import newsaggregator.model.BaseModel;

import java.util.List;

public class Content extends BaseModel {

    // Attributes

    private String link;
    private String source;
    private String type;
    private String title;
    private String detailedContent;
    private String creationDate;
    private String author;
    private List<String> categories;

    // Constructors

    public Content() {
    }

    public Content(String guid, String link, String source, String type, String title, String detailedContent, String creationDate, String author, List<String> categories) {
        super(guid, type);
        this.link = link;
        this.source = source;
        this.title = title;
        this.detailedContent = detailedContent;
        this.creationDate = creationDate;
        this.author = author;
        this.categories = categories;
    }

    // Methods

    public String getLink() {
        return link;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getDetailedContent() {
        return detailedContent;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getCategories() {
        return categories;
    }

    @Override
    public void display() {
        super.display();
        System.out.println("Link: " + link);
        System.out.println("Source: " + source);
        System.out.println("Title: " + title);
        System.out.println("Detailed Content: " + detailedContent);
        System.out.println("Creation Date: " + creationDate);
        System.out.println("Author: " + author);
        System.out.println("Categories: ");
        for (String category : categories) {
            System.out.println(category);
        }
    }
}
