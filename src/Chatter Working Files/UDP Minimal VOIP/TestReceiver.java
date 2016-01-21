import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class TestReceiver implements Runnable {
    
    private DatagramSocket dSocket;
    private DatagramPacket dPacket;
    byte[] bytes = new byte[1024];
    
    private Socket socket;
    
    private Client virtualClient;
    //private BufferedInputStream in;
    
    public TestReceiver(Socket socket, Client virtualClient) {
    	try {
        	this.socket = socket;
        	dSocket = new DatagramSocket(6678);
        	dPacket = new DatagramPacket(bytes, bytes.length);
        	this.virtualClient = virtualClient;

            //in = new BufferedInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }
    
    public void run() {
        Playback playback = new Playback();
        while(true) {
            try {
                dSocket.receive(dPacket);
                //in.read(dPacket.getData(), 0, dPacket.getData().length);
                playback.play(dPacket.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}