import java.io.*;

public class Contact implements Serializable {
    
    public static final int ACCEPTED = 0;
    public static final int DECLINED = 1;
    public static final int PENDING = 2;
    public static final int NOT_CALLED = 2;
    
    private String username;
    private int id;
    private int inCall;
     
    public Contact(String username, int id, int inCall) {
        this.username = username;
        this.id = id;
        this.inCall = inCall;
    }
    
    public Contact(String username){
     this.username = username;
    }
    
    public String getUsername() {
     return username;
    }
    
    public int getID() {
        return id;
    }
    
    public int getInCall() {
        return inCall;
    }
}