// Common parent class for all the records in the project.
// Every record has an id and knows how to write itself as one csv line.
public class BaseEntity {

    private int id;

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // child classes override this method to give their own csv line
    public String toCsv() {
        return String.valueOf(id);
    }
}
