/*	Author: Simon du Preez 
 *	Date: 2013.02.01
 * 
 *	Purpose: 
 *		Contains constants used within the program
 *	Version History:
 *		1.00 - Created Program
 */ 
public class Constants {
	//Server port and hostname
	public static final String HOSTNAME = "192.168.1.104";
	public static final int PORT = 6677;
	
	//Timeout for socket connection in milliseconds
	public static final int CONNECT_TIMEOUT = 4000;
	public static final int HANDSHAKE_TIMEOUT = 3000;
	
	//Registering states
	public static final int SUCCESSFUL_REGISTRATION = 2;
	public static final int FAILED_REGISTRATION = 1;
	public static final int NOT_REGISTERING = 0;
}
