/*Author: Nectarheart
 * Simple class to communicate with the client and setup calls with remote client to other clients.
 * Broadcasts and other operations which affect all clients will use this classes methods.
 */

package Server;

import java.net.*;
import ChatterIO.*;

public class ServerClient {
	
	//Remote clients connection
	private Socket connectedSock;
	private InetAddress userID;
	private int portNumber;
	
	//Class to write data to the Socket's output stream
	private SChatterWriter writer;
	
	ServerClient(Socket connectedSock, InetAddress userID, int portNumber) {
		this.connectedSock = connectedSock;
		this.userID = userID;
		this.portNumber = portNumber;
		writer = new SChatterWriter(this);
	}
	
	public Socket getSock() {
		return connectedSock;
	}
	
	//Method to retrieve clients Socket address
	public InetAddress getClientsSocketAddress() {
		return connectedSock.getInetAddress();
	}
	
	//Method to start a thread listening on the Socket's input stream
	public void startListening() {
		new Thread(new SChatterListener(this)).start();
	}
	
	//Method to send data to user by invoking writers send method
	public void toSend(byte b) {
		writer.send(b); 
	}
	
}