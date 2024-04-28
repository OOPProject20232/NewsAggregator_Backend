package newsaggregator.model;

public class BaseModel {

    // Attributes

    private String guid;
    public String type;

    // Constructors

    public BaseModel() {
    }

    public BaseModel(String guid, String type) {
        this.guid = guid;
        this.type = type;
    }

    // Methods

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void display() {
        System.out.println("Guid: " + guid);
        System.out.println("Type: " + type);
    }
}
