package ChatterIO;

import java.io.*;

import Server.ServerClient;

public class SChatterWriter {
	
	private OutputStream out;
	
	
	public SChatterWriter(ServerClient sClient) {
		try {
			out = sClient.getSock().getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(byte b) {
		try {
			out.write(b);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}