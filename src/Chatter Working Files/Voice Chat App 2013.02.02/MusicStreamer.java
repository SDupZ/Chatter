import javax.sound.sampled.*;
import java.io.*;

public class MusicStreamer implements Runnable {
    
    private Sender sender;
    private AudioInputStream stream;
    
    public MusicStreamer(Sender sender) {
        this.sender = sender;
        try{
            stream = AudioSystem.getAudioInputStream(new File("C:/test.wav"));
            System.out.println(stream.getFormat());
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        int numOfBytes = 0;
        byte[] audioBytes = new byte[512];
        while(numOfBytes != -1) {
            try {
                numOfBytes = stream.read(audioBytes, 0, audioBytes.length);
                sender.forward(new Package(Package.MUSIC, audioBytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

