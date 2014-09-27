package Server;

import java.net.*;
import java.io.*;

public class ServerClient implements Runnable {
	
	Socket connectedSock;
	int userNumber;
	int portNumber;
	
	ServerClient(Socket connectedSock, int userNumber, int portNumber) {
		this.connectedSock = connectedSock;
		this.userNumber = userNumber;
		this.portNumber = portNumber;
	}
	
	public void run() {
		try {
			byte[] b;
			String toRead;
			InputStream toRecv = connectedSock.getInputStream();
			OutputStream toSend = connectedSock.getOutputStream();
			while(true) {
				b = new byte[20];
				if (toRecv.available() > 0) {
					toRecv.read(b);
					toRead = new String(b);
					System.out.println(toRead + " Success");
					toSend.write(b);
					toSend.flush();
				}
			}
		} catch (IOException exception) {
			System.out.println(exception.toString());
		}
	}
}