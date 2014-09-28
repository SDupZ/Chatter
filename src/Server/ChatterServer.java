package Server;
	
import java.net.*;
import java.io.*;

public class ChatterServer {
	private final static int _PORT = 3001;
	private final static int _MAX_USERS = 10;
	
	ServerSocket server;
	
	public void start(){
		ServerClient[] users;
		int userCounter = 0;
		users = new ServerClient[_MAX_USERS];
		System.out.println("Hello");
		try {
			server = new ServerSocket(_PORT);
			while(true) {
				users[userCounter] = new ServerClient(server.accept(), userCounter, _PORT);
				users[userCounter].startListening();
				System.out.println("Connection Successful");
				userCounter++;
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