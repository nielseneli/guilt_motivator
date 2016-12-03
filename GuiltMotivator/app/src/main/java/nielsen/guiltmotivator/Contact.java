package nielsen.guiltmotivator;

/**
 * Created by zlan on 11/7/16.
 *
 */
public class Contact {
    private String name;
    private String method;
    private String address;
    private long id;

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

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


}
