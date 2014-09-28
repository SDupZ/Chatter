package ChatterIO;

import java.io.*;
import java.util.Scanner;

import Client.ChatterClient;

public class ChatterWriter implements Runnable {
	
	private ChatterClient client;
	
	
	public ChatterWriter(ChatterClient client) {
		this.client = client;
	}
	
	public void run() {
		String toSend;
		OutputStream out;
		Scanner reader = new Scanner(System.in);
		try {
			out = client.getSock().getOutputStream();
			while(true) {
				toSend = reader.next();
				if (toSend.substring(0, 2).equals("*C")) {
					System.out.println("KKL");
					client.clientConnect(toSend.substring(2), reader.nextInt());
				} else if (toSend.substring(0, 2).equals("*U")) {
					client.getCallUser(Integer.parseInt(toSend.substring(2, 3))).getOutputStream().write(toSend.substring(3).getBytes());	
				} else {
					out.write(toSend.getBytes());
					out.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}