import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class ClientReceiver implements Runnable {
    
    private Socket socket;
    private ObjectInputStream in;
    private Package currentPack;
    private Playback player;
    private Playback musicPlayer;
    private Thread t;
    private Thread s;
    private ClientGUI gui;
    
    public ClientReceiver(Socket socket, ClientGUI gui) {
        this.socket = socket;
        this.gui = gui;
        
        player = new Playback(new AudioFormat(8000.0f, 16, 1, true, true));        
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }    
    	
    public void run() {
        while(true) {
            try {
                currentPack = (Package)in.readObject();
                
                if(currentPack.getType() == Package.MUSIC) {
                	if(t != null){                	
                		t.join();
                	}
                	t = null;           
                	musicPlayer.setAudioBytes(currentPack.getInfo());
                	t = new Thread(musicPlayer);
                	t.start();
                }else if(currentPack.getType() == Package.VOICE){
                	if(s != null){                	
                		s.join();
                	}
                	s = null;           
                	player.setAudioBytes(currentPack.getInfo());
                	s = new Thread(player);
                	s.start();  
                }else if (currentPack.getType() == Package.TEXT) {
	                 if(gui.isVerifiedByServer()){
	                 	//text messages
	                 }else if(new String(currentPack.getInfo()).equals("failedLogin")){
	                 	gui.setfailedLogin(true);
	                 }                 
                	String text = new String(currentPack.getInfo());
                }else if (currentPack.getType() == Package.CONTACTS){
                	if(gui.isVerifiedByServer()){          	
	                 	gui.updateContactsList(currentPack.getContacts());	                 	
                	}else{     		  
                		gui.setContactsList(currentPack.getContacts());                		
                		gui.setVerifiedByServer(true);
                	}
                }else if (currentPack.getType() == Package.MUSIC_REQUEST){
                	musicPlayer = new Playback(getAudioFormatFromByteArray(currentPack.getInfo()));
                }else if (currentPack.getType() == Package.REQUEST){
                 	gui.incomingCall(currentPack.getContact(), currentPack.getContacts());
                }else if (currentPack.getType() == Package.ACCEPTED){                	
                	gui.hasAcceptedCall(currentPack.getContact());
                	if(!gui.getCallHasStarted()){
                		gui.setCallHasStarted(true);
                	}
                }else if (currentPack.getType() == Package.DECLINED){
                	gui.hasDeclinedCall(currentPack.getContact());
	                if(!gui.getCallHasStarted()){
	                	gui.setCallHasStarted(false);
	                }                	
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Foreign object sent");
                break;
            }catch (Exception e){
         		e.printStackTrace();
        	}
        }
    }
    
    public Package receive() {
        return null;
    }
    	//---------------------------------------------new music sent from somebody else
	public AudioFormat getAudioFormatFromByteArray(byte[] audioByteFormat){
		AudioFormat.Encoding encode = null;
		if (audioByteFormat[0] == 0) {
			encode = AudioFormat.Encoding.ALAW;
		} else if (audioByteFormat[0] == 1) {
			encode = AudioFormat.Encoding.PCM_SIGNED;
		} else if (audioByteFormat[0] == 2) {
			encode = AudioFormat.Encoding.PCM_UNSIGNED;
		} else if (audioByteFormat[0] == 3) {
			encode = AudioFormat.Encoding.ULAW;
		}
		float sampleRate = (new Byte(audioByteFormat[1])).floatValue();
		int sampleSizeInBits =(new Byte(audioByteFormat[2])).intValue();
		int channels =(new Byte(audioByteFormat[3])).intValue();
		int frameSize =(new Byte(audioByteFormat[4])).intValue();
		float frameRate = (new Byte(audioByteFormat[5])).floatValue();
		boolean bigEndian;
		if (audioByteFormat[6] == 0) {
			bigEndian = false;
		} else {
			bigEndian = true;
		}
		AudioFormat format =  new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f, false);//encode, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
		System.out.println(sampleRate);
		return format;
	}
}