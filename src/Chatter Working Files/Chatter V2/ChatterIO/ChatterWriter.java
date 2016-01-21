/*Author: Nectarheart
 * Simple class that writes data to the specified socket's output stream.
 * Currently only writes data from the console to specified user (unspecified is sent to server).
 */

package ChatterIO;

import java.io.*;
import java.net.InetAddress;
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
				
				//Note that this if statement is for calling another user
				if (toSend.substring(0, 2).equals("*C")) {
					System.out.println("KKL");
					client.clientConnect(toSend.substring(2), reader.nextInt());
				//Else if for sending data to a specific user currently connected to this writer's client
				} else if (toSend.substring(0, 2).equals("*U")) {
					client.getCallUser(InetAddress.getByName(toSend.substring(2))).getOutputStream().write(reader.next().getBytes());
				//Else if for printing this writer's currently called users
				} else if (toSend.substring(0, 2).equals("*G")) {
					client.printCurrentCallUsers();
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