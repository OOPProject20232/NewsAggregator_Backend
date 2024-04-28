package newsaggregator.model;

public class BaseModel {

    // Attributes

    private String guid;

    // Constructors

    public BaseModel() {
    }

    public BaseModel(String guid) {
        this.guid = guid;
    }

    // Methods

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void display() {
        System.out.println("Guid: " + guid);
    }
}
