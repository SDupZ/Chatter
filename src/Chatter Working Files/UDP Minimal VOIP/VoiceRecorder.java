import javax.sound.sampled.*;
import java.io.*;

public class VoiceRecorder implements Runnable {
    
    private TargetDataLine line;
    private AudioFormat format;
    private TestSender sender;
    
    public VoiceRecorder(TestSender sender) {
        this.sender = sender;
        format = new AudioFormat(8000.0f, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();//System.out.println("Line in use");
        }
    }
    public void run() {
        try {
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();//System.out.println("Line in use");
            //System.exit(1);
        }
        System.out.println("Voice Recorder Started");
        while(true) {
            byte[] audioBytes = new byte[1024];
            line.read(audioBytes, 0, audioBytes.length);           
            sender.forward(audioBytes);
        }
    }
}