package Server;

import java.util.*;
import java.net.*;
import java.io.*;

public class ChatterServer {
	
	String input;
	int _PORT = 3001;
	int _MAX_USERS = 10;
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
				new Thread(users[userCounter]).start();
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
	}//Hello
}