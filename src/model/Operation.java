package model;

public class Operation {
    private String opId;
    private String opName;
    private String description;

    public Operation(String opId, String opName, String description) {
        this.opId = opId;
        this.opName = opName;
        this.description = description;
    }

    // A special trick: We override toString() so the Dropdown shows the Name, not the memory address
    @Override
    public String toString() {
        return opName;
    }

    public String getOpId() { return opId; }
    public String getOpName() { return opName; }
    public String getDescription() { return description; }
}
