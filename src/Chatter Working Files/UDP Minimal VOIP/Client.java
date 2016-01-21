import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class Client {
    private static final int PORT = 6677;
    private static final String HOSTNAME = "192.168.2.6";
    private static final int SIZE = 64;
    
    private Socket socket;
    private TestSender sender;
    private TestReceiver receiver;
    
    public Client() {
        try {
        	System.out.println("Waiting for connection.");
            socket = new ServerSocket(PORT).accept();
            System.out.println("Connected.");
            //connect();  
            sender = new TestSender(socket);
            receiver = new TestReceiver(socket, this);
            VoiceRecorder vRec = new VoiceRecorder(sender);
            new Thread(vRec).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
