/*Author: Nectarheart
 * Class that provides connection to server and other clients for calls. Contains all current user information as well as other clients 
 * who are currently in a call with this client.
 * More information as well as accessor and mutator methods will be added with later versions.
 */

package Client;

import java.net.*;
import java.io.*;
import ChatterIO.*;
import java.util.*;

public class ChatterClient {
	
	//Server's port to accept connections
	private static final int _HOST_PORT = 3001;
	
	//Server's address
	static final String _ADDRESS = "localhost";
	
	//Port that this client will accept connections on
	private final static int _ACCPETING_PORT = 3002;
	
	private Socket hostSock;
	
	//Hashmap of other users currently in a call with this user
	private HashMap<InetAddress, Socket> callUsers;
	private ServerSocket socketAcceptor;
	private InetSocketAddress hostServerAddress;
	
	public void start() {
		hostServerAddress = new InetSocketAddress(_ADDRESS, _HOST_PORT);
		hostSock = new Socket();
		callUsers = new HashMap<InetAddress, Socket>();
		try {
			socketAcceptor = new ServerSocket(_ACCPETING_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Socket incomingCaller;
			hostSock.connect(hostServerAddress);
			System.out.println("Connection Successful");
			new Thread(new ChatterListener(hostSock)).start();
			new Thread(new ChatterWriter(this)).start();
			
			//Listens on Server Socket for incoming calls from other users 
			while (true) {
				incomingCaller = socketAcceptor.accept();
				callUsers.put(incomingCaller.getInetAddress(), incomingCaller);
				new Thread(new ChatterListener(callUsers.get(incomingCaller.getInetAddress()))).start();
				System.out.println("Connected successfully to: " + callUsers.get(incomingCaller.getInetAddress()).getRemoteSocketAddress().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public Socket getSock() {
		return hostSock;
	}
	
	//Method to connect this user with another user who this user is attempting to call
	public void clientConnect(String address, int port) {
		Socket userToCall = new Socket();
		System.out.println(address + " " + port);
		try {
			userToCall.connect(new InetSocketAddress(address, port));
			callUsers.put(userToCall.getInetAddress(), userToCall);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getCallUser(InetAddress userID) {
		return callUsers.get(userID);
	}
	
	//Method to retrieve all  addresses of current users in a call with this user
	public ArrayList<String> getCallUsers() {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Map.Entry<InetAddress, Socket>> it = callUsers.entrySet().iterator();
		while (it.hasNext()) {
			list.add(it.next().getKey().toString());	
		}
		return list;
	}
	
	public void printCurrentCallUsers() {
		Iterator<String> it = getCallUsers().iterator();
		while (it.hasNext()) {
			System.out.println(it.next());	
		}
	}
	
	public static void main(String[] args) {
		ChatterClient program = new ChatterClient();
		program.start();
	}
	
}