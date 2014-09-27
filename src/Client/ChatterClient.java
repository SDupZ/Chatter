package Client;

import java.net.*;
import java.io.*;
import java.util.*;

public class ChatterClient {
	
	Socket connectingSock;
	InetSocketAddress hostServer;
	static final int _HOST_PORT = 3001;
	static final String _ADDRESS = "localhost";
	Scanner reader;
	
	public void start() {
		String toSend;
		OutputStream out;
		InputStream in;
		int b;
		reader = new Scanner(System.in);
		hostServer = new InetSocketAddress(_ADDRESS, _HOST_PORT);
		connectingSock = new Socket();
		try {
			connectingSock.connect(hostServer);
			System.out.println("Connection Successful");
			out = connectingSock.getOutputStream();
			in = connectingSock.getInputStream();
			while (true) {
				toSend = reader.next();
				out.write(toSend.getBytes());
				out.flush();
				while ((b = in.read()) != -1) {
					System.out.print((char)b);
				}
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