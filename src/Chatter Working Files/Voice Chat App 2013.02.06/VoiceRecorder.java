import javax.sound.sampled.*;
import java.io.*;

public class VoiceRecorder implements Runnable {
    
    private TargetDataLine line;
    private AudioFormat format;
    private Sender sender;
    private boolean inCall;
    
    public VoiceRecorder(Sender sender) {
    	inCall = false;
        this.sender = sender;
        format = new AudioFormat(8000.0f, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();//System.out.println("Line in use");
            //System.exit(1);
        }
    }
    
    public void setInCall(boolean inCall){
    	this.inCall = inCall;
    }
    public void run() {
        try {
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();//System.out.println("Line in use");
            //System.exit(1);
        }
        while(inCall) {
            byte[] audioBytes = new byte[line.getBufferSize() / 10];
            line.read(audioBytes, 0, audioBytes.length);
            sender.forward(new Package(Package.VOICE, audioBytes));
        }
    }
}