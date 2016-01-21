import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class ClientReceiver implements Runnable {
    
    private Socket socket;
    private ObjectInputStream in;
    private Package currentPack;
    private Playback player;
    private Playback musicPlayer;
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
    	try {
        	while(true) {
        		//~~~~~~~~~~~~~~~~~~~~~~~Wait for new package
                currentPack = (Package)in.readObject();
                
                //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //++++VOICE++++
                if(currentPack.getType() == Package.VOICE){       
                	player.play(currentPack.getInfo());
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
	            //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //++++Contacts++++
                }else if (currentPack.getType() == Package.CONTACTS){
                	if(gui.isVerifiedByServer()){                		     
	                 	gui.updateContactsList(currentPack.getContacts());	                 	
                	}else{   
                		gui.setContactsList(currentPack.getContacts());                		
                		gui.setVerifiedByServer(true);
                	}        	
                }else if (currentPack.getType() == Package.ACCEPT_CONTACT){
                	gui.addContact(currentPack.getContact()); 
                }else if (currentPack.getType() == Package.DECLINE_CONTACT){
                }else if (currentPack.getType() == Package.ADD_CONTACT){
                	//DEBUG------ this should ask user to accept ---- current just assumes accept.                	
                	gui.acceptContact(currentPack.getContacts(), currentPack.getContact(), currentPack.getInfo());
                	gui.addContact(currentPack.getContact());  	                
                //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //++++TEXT++++              
                }else if (currentPack.getType() == Package.TEXT) {
	                 if(gui.isVerifiedByServer()){
	                 	//text messages
	                 }else{
	                 	if(new String(currentPack.getInfo()).equals("failedLogin")){
	                 		gui.setfailedLogin(true);
	                 	}else if(new String(currentPack.getInfo()).equals("duplicateLogin")){
	                 		gui.setDuplicateLogin(true);
	                 	}else if(new String(currentPack.getInfo()).equals("failedRegistration")){
	                 		gui.setRegistered(Constants.FAILED_REGISTRATION);
	                 		break;
	                 	}else if(new String(currentPack.getInfo()).equals("successfulRegistration")){
	                 		gui.setRegistered(Constants.SUCCESSFUL_REGISTRATION);
	                 		break;
	                 	}            
	                 }	           	              	
                //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //++++MUSIC++++
                }else if(currentPack.getType() == Package.MUSIC) {
                }else if (currentPack.getType() == Package.MUSIC_REQUEST){
                }   
        	}
        }catch (SocketException e){
     		System.out.println("Fatal Error: Connection reset by server");
     		System.exit(1);
    	}catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            System.out.println("Foreign object sent");         
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