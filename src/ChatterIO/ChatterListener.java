package ChatterIO;

import java.io.*;
import java.net.*;

public class ChatterListener implements Runnable {
	
	private Socket clientSock;
	
	public ChatterListener(Socket clientSock) {
		this.clientSock = clientSock;
	}
	
	public void run() {
		InputStream in;
		int b;
		try {
			in = clientSock.getInputStream();
			while ((b = in.read()) != -1) {
				System.out.print((char)b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}