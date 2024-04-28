package newsaggregator.model.content;

public class Publisher {
    private String publisherName;
    private String publisherLink;
    private String publisherLogo;

    public Publisher() {
    }

    public Publisher(String publisherName, String publisherLink, String publisherLogo) {
        this.publisherName = publisherName;
        this.publisherLink = publisherLink;
        this.publisherLogo = publisherLogo;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherLink() {
        return publisherLink;
    }

    public void setPublisherLink(String publisherLink) {
        this.publisherLink = publisherLink;
    }

    public String getPublisherLogo() {
        return publisherLogo;
    }

    public void setPublisherLogo(String publisherLogo) {
        this.publisherLogo = publisherLogo;
    }
}
