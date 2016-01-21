import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class Sender implements Runnable {
    
    Socket socket;
    ObjectOutputStream out;
    
    public Sender(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() { 
    }
    
    public synchronized void forward(Package pack) {
        try {
            out.writeObject(pack);
            out.flush();
            out.reset();
            
        } catch (IOException e) {
            System.out.println("Socket has been reset and can no longer send data");
        }
    }
}