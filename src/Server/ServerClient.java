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
			int b;
			InputStream toRecv = connectedSock.getInputStream();
			OutputStream toSend = connectedSock.getOutputStream();
			while(true) {
				if (toRecv.available() > 0) {
					while ((b = toRecv.read()) != -1)
					System.out.print((char)b);
					toSend.write(b);
					toSend.flush();
				}
			}
		} catch (IOException exception) {
			System.out.println(exception.toString());
		}
	}
}