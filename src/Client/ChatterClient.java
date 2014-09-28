package Client;

import java.net.*;
import java.io.*;
import ChatterIO.*;

public class ChatterClient {
	private static final int _HOST_PORT = 3001;
	static final String _ADDRESS = "localhost";
	private final static int _ACCPETING_PORT = 3002;
	private final static int _MAX_USERS = 10;
	
	private Socket hostSock;
	private Socket[] callUsersR;
	private Socket[] callUsers;
	private int callCounterR;
	private int callCounter;
	private ServerSocket socketAcceptor;
	private InetSocketAddress hostServer;
	
	public void start() {
		hostServer = new InetSocketAddress(_ADDRESS, _HOST_PORT);
		hostSock = new Socket();
		callUsersR = new Socket[_MAX_USERS];
		callUsers = new Socket[_MAX_USERS];
		try {
			socketAcceptor = new ServerSocket(_ACCPETING_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		callCounterR = 0;
		callCounter = 0;
		try {
			hostSock.connect(hostServer);
			System.out.println("Connection Successful");
			new Thread(new ChatterListener(hostSock)).start();
			new Thread(new ChatterWriter(this)).start();
			while (true) {
				callUsersR[callCounter] = socketAcceptor.accept();
				new Thread(new ChatterListener(callUsersR[callCounterR])).start();
				System.out.println("Connected successfully to: " + callUsersR[callCounterR].getRemoteSocketAddress().toString());
				callCounterR++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public Socket getSock() {
		return hostSock;
	}
	
	public void clientConnect(String address, int port) {
		callUsers[callCounter] = new Socket();
		System.out.println(address + " " + port);
		try {
			callUsers[callCounter].connect(new InetSocketAddress(address, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
		callCounter++;
	}
	
	public Socket getCallUser(int userNumber) {
		return callUsers[userNumber];
	}
	
	public static void main(String[] args) {
		ChatterClient program = new ChatterClient();
		program.start();
	}
	
}