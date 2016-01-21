import javax.sound.sampled.*;
import java.io.*;
    
public class Package implements Serializable {
    
    static final int TEXT = 0;
    static final int VOICE = 1;
    static final int FILE = 2;
    static final int REGISTER = 3;
    static final int CONTACTS = 4;
    static final int REQUEST = 5;
    static final int ACCEPTED = 6;
    static final int DECLINED = 7;
    static final int MUSIC_REQUEST = 8;
    static final int MUSIC = 9;
    static final int ADD_CONTACT = 10;
    
    private int type;
    private byte[] info;
    private Contact[] contacts;
    private Contact contact;
    
    public Package(int type, byte[] info) {
        this.type = type;
        this.info = info;
    }
    
    public Package(int type, Contact[] contacts, byte[] info) {
        this.type = type;
        this.contacts = contacts;
        this.info = info;
    }

    public Package(int type, Contact[] contacts, Contact contact, byte[] info) {
        this.type = type;
        this.contacts = contacts;
        this.contact = contact;
        this.info = info;
    }
    
    public int getType() {
        return type;
    }
    
    public byte[] getInfo() {
        return info;
    }
    
    public Contact[] getContacts() {
        return contacts;
    }
    
    public Contact getContact() {
        return contact;
    }
}