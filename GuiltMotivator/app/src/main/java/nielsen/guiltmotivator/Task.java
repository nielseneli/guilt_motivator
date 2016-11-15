package nielsen.guiltmotivator;


/**
 *A task class. It includes a string of the thing you have to do and a row id.
 */
public class Task {
    private String text;
    private long id;
    private boolean isChecked;

    public Task() {

    }


    public Task(String text) {
        this.text = text;
        this.isChecked = false;
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


    public boolean isChecked() {
        return isChecked;
    }

    public void toggleChecked() {
        this.isChecked = !isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}