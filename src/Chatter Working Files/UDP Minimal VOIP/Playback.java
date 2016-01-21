import javax.sound.sampled.*;
import java.io.*;

public class Playback{
    
    private SourceDataLine line;
    private AudioFormat audioFormat;
    
    public Playback(){
        try{     
            line = null;       
            audioFormat = new AudioFormat(8000.0f, 16, 1, true, true);        
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);               
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();         
        }catch(Exception e){
            e.printStackTrace();
        }   
    }    
    
    public void play(byte[] audioBytes) {
        try{     
            line.write(audioBytes, 0, audioBytes.length);
        }catch(Exception e){
        }
    }
}