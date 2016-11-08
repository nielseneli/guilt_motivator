package nielsen.guiltmotivator;

/**
 * Created by zlan on 11/7/16.
 */
public class Contact {
    private String name;
    private String method;

    public Contact(String name, String method){
        this.name = name;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }
}
