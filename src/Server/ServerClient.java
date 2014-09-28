package Server;

import java.net.*;
import ChatterIO.*;

public class ServerClient {
	
	private Socket connectedSock;
	private int userNumber;
	private int portNumber;
	private SChatterWriter writer;
	
	ServerClient(Socket connectedSock, int userNumber, int portNumber) {
		this.connectedSock = connectedSock;
		this.userNumber = userNumber;
		this.portNumber = portNumber;
	}
	
	public Socket getSock() {
		return connectedSock;
	}
	
	public void startListening() {
		new Thread(new SChatterListener(this)).start();
		writer = new SChatterWriter(this);
	}
	
	public void toSend(byte b) {
		writer.send(b); 
	}
	
}