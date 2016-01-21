import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class Sender{
    
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
    
    public synchronized void forward(Package pack){
    	//new Thread(new Runnable(){
    	//	public void run(){   
    			try{    				 		
		            out.writeObject(pack);
		            out.flush();
		            out.reset();
    			}catch(IOException e){
    				e.printStackTrace();
    				//System.out.println("Socket has been reset");
    			}			                 
    		//}    		
    	//}).start();    	
    }
}