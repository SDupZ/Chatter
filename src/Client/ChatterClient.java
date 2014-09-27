package Client;

import java.net.*;
import java.io.*;
import java.util.*;

public class ChatterClient {
	
	Socket connectingSock;
	InetSocketAddress hostServer;
	static final int _HOST_PORT = 3001;
	Scanner reader;
	
	public void start() {
		String toSend;
		OutputStream out;
		reader = new Scanner(System.in);
		hostServer = new InetSocketAddress("localhost", _HOST_PORT);
		connectingSock = new Socket();
		try {
			connectingSock.connect(hostServer);
			System.out.println("Connection Successful");
			out = connectingSock.getOutputStream();
			while (true) {
				toSend = reader.next();
				out.write(toSend.getBytes());
				out.flush();
			}
		} catch (IOException exception) {
			System.out.println("Connection error, Connection was denied");
		}
	}
	
	public static void main(String[] args) {
		ChatterClient program = new ChatterClient();
		program.start();
	}
	
}