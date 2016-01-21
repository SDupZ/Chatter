import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestSender {
    
    private Socket socket;
    private DatagramSocket dSocket;
    private DatagramPacket dPacket;
    private BufferedOutputStream out;
    
    public TestSender(Socket socket) {
    	try {
        	this.socket = socket;
        	dSocket = new DatagramSocket();
        
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void forward(byte[] bytes) {
        try {
        	dPacket = new DatagramPacket(bytes, bytes.length, socket.getInetAddress(), 6678);
        	dSocket.send(dPacket);
            //out.write(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}