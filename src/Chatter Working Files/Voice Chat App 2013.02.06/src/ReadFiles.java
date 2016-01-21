import java.io.*;

public class ReadFiles {
    
    public static void sendFile(Sender sender, File file, Contact[] contacts) throws IOException {
    	FileInputStream in;
        int numOfBytes = 0;
        byte[] bytes = new byte[64];
        in = new FileInputStream(file);
        while (numOfBytes != -1) {
            numOfBytes = in.read(bytes, 0, bytes.length);
            sender.forward(new Package(Package.FILE, contacts, bytes));
        }
    }
}