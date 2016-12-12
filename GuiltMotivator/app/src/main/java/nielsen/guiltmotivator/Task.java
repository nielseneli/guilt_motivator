package nielsen.guiltmotivator;


import java.util.Calendar;

/**
 *A task class. It includes a string of the thing you have to do, a row id, a due date and a boolean of whether it's done.
 */
public class Task {
    private String text;
    private long id;
    private boolean isChecked;
    private Calendar dueDate;

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

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }
}