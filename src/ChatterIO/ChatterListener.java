/*Author: Nectarheart
 * Simple class that listens on the client's socket's input stream for incoming data.
 * One instance of this class is for incoming calls.
 * The other instance is for individual calls to other users and for communication with the server.
 */

package ChatterIO;

import java.io.*;
import java.net.*;

public class ChatterListener implements Runnable {
	
	private Socket clientSock;
	
	public ChatterListener(Socket clientSock) {
		this.clientSock = clientSock;
	}
	
	//Currently only prints data to the console
	public void run() {
		InputStream in;
		int b;
		try {
			in = clientSock.getInputStream();
			while ((b = in.read()) != -1) {
				System.out.print((char)b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}