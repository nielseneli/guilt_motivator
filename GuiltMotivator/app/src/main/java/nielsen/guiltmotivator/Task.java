package nielsen.guiltmotivator;


/**
 *A task class. It includes a string of the thing you have to do and a row id.
 */
public class Task {
    private String text;
    private long id;

    public Task() {

    }

    public Task(String text) {
        this.text = text;
    }

    public Task(String text, long id) {
        this.text = text;
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}