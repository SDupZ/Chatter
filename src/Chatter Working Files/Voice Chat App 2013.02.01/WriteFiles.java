import java.io.*;

public class WriteFiles { 
    
    public static void receiveFile(byte[] bytes, File file, Contact[] contacts) throws IOException {
    	FileOutputStream out;
        out = new FileOutputStream(file);
        out.write(bytes, 0, bytes.length);
    }
}