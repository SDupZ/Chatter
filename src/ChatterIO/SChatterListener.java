package ChatterIO;

import java.io.*;

import Server.ServerClient;

public class SChatterListener implements Runnable {
	
	private ServerClient sClient;
	
	public SChatterListener(ServerClient sClient) {
		this.sClient = sClient;
	}
	
	public void run() {
		InputStream in;
		int b;
		try {
			in = sClient.getSock().getInputStream();
			while ((b = in.read()) != -1) {
				System.out.print((char)b);
				sClient.toSend((byte)b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}