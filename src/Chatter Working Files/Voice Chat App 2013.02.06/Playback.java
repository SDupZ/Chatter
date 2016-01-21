import javax.sound.sampled.*;

public class Playback{
    
    private SourceDataLine line;
    private boolean voice;
    private byte[] audioBytes;
    
    public Playback(AudioFormat format) {
        line = null;
        //AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        //AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f, false);
        
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        //Line.Info info = remix.getSourceLineInfo()[0];

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();//System.out.println("Line in use");
        }
    }    
    
    public void play(byte[] audioBytes) {
    	line.write(audioBytes, 0, audioBytes.length);
    }
}