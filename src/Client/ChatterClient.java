package Client;

import java.net.*;
import java.io.*;
import java.util.*;

public class ChatterClient {
	private static final int _HOST_PORT = 3001;
	static final String _ADDRESS = "203.96.194.80";
	
	private Socket connectingSock;
	private InetSocketAddress hostServer;
	private Scanner reader;
	
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