/*Author: Nectarheart
 * 
 * Simple class to listen for incoming connections made by client.
 * All client specific server operations are handled by the ServerClient class.
 * Any broadcasts and other operations that affect all clients will be done in another thread.
*/

package Server;
	
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatterServer {
	private final static int _PORT = 3001;
	
	private ServerSocket server;
	
	//Hash map to store users information server side. Will implement more sophisticated ID in later versions
	private HashMap <InetAddress, ServerClient> users;
	
	public void start(){
		users = new HashMap<InetAddress, ServerClient>();
		Socket incomingClient;
		System.out.println("Hello");
		try {
			server = new ServerSocket(_PORT);
			while(true) {
				incomingClient = server.accept();
				users.put(incomingClient.getInetAddress(), new ServerClient(incomingClient, incomingClient.getInetAddress(), _PORT));
				users.get(incomingClient.getInetAddress()).startListening();
				System.out.println("Connection Successful");
			}		
		} catch (IOException exception) {
			System.out.println(exception.toString());
			exception.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChatterServer program =  new ChatterServer();
		program.start();
	}
}