package model;

public class Theater {
    private String theaterId;
    private String name;

    public Theater(String theaterId, String name) {
        this.theaterId = theaterId;
        this.name = name;
    }

    public String getTheaterId() { return theaterId; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + theaterId + ")";
    }
}
