package nielsen.guiltmotivator;

/**
 * Contains name, method, address, id of the task it's associated with, and its own row id from sql.
 *
 */
public class Contact {
    private String name;
    private String method;
    private String address;
    private long taskId;
    private long localId;
    private String tone;

    public Contact(String name, String method, String address){
        this.name = name;
        this.method = method;
        this.address = address;
    }

    public String getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTaskId(long id) {
        this.taskId = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setLocalId(long id) {
        this.localId = id;
    }

    public long getLocalId() {
        return localId;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getTone() {
        return tone;
    }
}
