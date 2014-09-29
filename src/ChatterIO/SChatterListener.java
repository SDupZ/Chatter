/*Author: Nectarheart
 * Simple class that listens on the server client's socket's input stream for incoming data.
 * Only listens on one socket per server client instance.
 */

package ChatterIO;

import java.io.*;

import Server.ServerClient;

public class SChatterListener implements Runnable {
	
	private ServerClient sClient;
	
	public SChatterListener(ServerClient sClient) {
		this.sClient = sClient;
	}
	
	//Currently only prints data to the console and mirrors it back to the user
	public void run() {
		InputStream in;
		int b;
		try {
			in = sClient.getSock().getInputStream();
			while ((b = in.read()) != -1) {
				System.out.print((char)b);
				sClient.toSend((byte)b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}