/*	Author: Simon du Preez 
 *	Date: 2012.08.22
 *
 *	Version History:
 *		1.00 - Created Program
 */ 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager.*;
import java.net.*;
import java.io.*;

public class ClientLoginGUI extends JFrame implements ActionListener{
	
	public static String HOSTNAME = Constants.HOSTNAME;
	public static int PORT = Constants.PORT;	
	public static int CONNECT_TIMEOUT = Constants.CONNECT_TIMEOUT;	
	public static int HANDSHAKE_TIMEOUT = Constants.HANDSHAKE_TIMEOUT;
	
	private Socket socket;
	private int timeoutCounter;
	
	//ClientLogin Gui Components
	private JFrame frame;	
	private JTextField username;
	private JPasswordField password;	
	private JButton login;
	private JButton register;
	
	//Login Stuff
	private Sender sender;
	private ClientReceiver receiver;
	private ClientGUI gui;
	private Timer t;
	
	
	//Register Stuff
	private JTextField registerUsername;
	private JPasswordField registerPassword;
	private JPasswordField registerPasswordConfirm;
	
	public ClientLoginGUI(){				
		gui = new ClientGUI();	
		
		//~~~~~~~~~~~~~~~~~~~~~~~Create Timer
		t = new Timer(50, this);
		t.setActionCommand("timer");
		timeoutCounter = 0;
		
		//~~~~~~~~~~~~~~~~~~~~~~~Create login ui
		changeLookAndFeel();
		createPasswordLoginGUI();
	}
	//****************************************************************************************************
	//***Action Performed
	//****************************************************************************************************
	public void actionPerformed(ActionEvent e){		
		//~~~~~~~~~~~~~~~~~~~~~~~Login button pressed
		if(e.getActionCommand().equals("login")){
			login.setEnabled(false);			
			register.setEnabled(false);
			
			new Thread(new Runnable(){
				public void run(){			
					if(tryConnect()){						
						sender.forward((new Package(Package.TEXT,new String(username.getText() + "\n" + new String(password.getPassword())).getBytes())));		
						t.start();											
					}else{
						new JOptionPane().showMessageDialog(frame, "Could not establish a valid connection \nwith the server. ");	
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								login.setEnabled(true);
								register.setEnabled(true);	
							}
						});								
					}										
				}
			}).start();			
		//~~~~~~~~~~~~~~~~~~~~~~~Register button pressed.
		}else if(e.getActionCommand().equals("register")){
			createRegisterForm();
			
		//~~~~~~~~~~~~~~~~~~~~~~~gui queried every 50 milliseconds to check for response from server.
		}else if(e.getActionCommand().equals("timer")){	
			timeoutCounter++;						
			if(gui.isVerifiedByServer()){
				//Response has been recieved from server to verifify login details.
				t.stop();									
				frame.dispose();
				gui.createGUI(sender);				
			}
			if(gui.getFailedLogin()){
				//Response has been recieved but login details were incorrect
				reset();
				new JOptionPane().showMessageDialog(frame, "Login failed. \nInvalid Username or Password.");
				
			}
			//The divided by 50 is because the timer is set to query every 50 milliseconds.
			if(timeoutCounter > (HANDSHAKE_TIMEOUT / 50)){
				//No response from server.
				reset();
				new JOptionPane().showMessageDialog(frame, "Server Handshake Failed. No response from server.");				
			}
		}	
	}
	//****************************************************************************************************
	//***Helper methods
	//****************************************************************************************************
	//---------------------------------------------Reset clientlogin after failed login.
	public void reset(){
		if(t.isRunning()){		
			t.stop();			
		}
		if(socket != null && !socket.isClosed()){
			try{				
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		password.setText("");
		login.setEnabled(true);
		register.setEnabled(true);
		gui.setfailedLogin(false);
		timeoutCounter = 0;
		
	}	
	//---------------------------------------------Register Form
	private void createRegisterForm(){
		JDialog dialog = new JDialog();
		JPanel registerForm = new JPanel();
		
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);		
		dialog.setTitle("Register");
		dialog.setLocationRelativeTo(frame);
		
		registerForm.setLayout(new GridLayout(3, 2));
		registerForm.setBorder(BorderFactory.createTitledBorder("Register Form"));
		
		registerUsername = new JTextField("Username Here");
		registerPassword = new JPasswordField("");
		registerPasswordConfirm = new JPasswordField("");
		
		registerForm.add(new JLabel("Username"));
		registerForm.add(registerUsername);
		registerForm.add(new JLabel("Password"));
		registerForm.add(registerPassword);
		registerForm.add(new JLabel("Confirm Password"));
		registerForm.add(registerPasswordConfirm);
		
		dialog.add(registerForm);
		dialog.pack();
		dialog.setVisible(true);
	}
	//---------------------------------------------Connect to the server
	private boolean tryConnect(){
		try{
			socket = new Socket();
			socket.connect(new InetSocketAddress(HOSTNAME, PORT), CONNECT_TIMEOUT);
			sender = new Sender(socket);						
			
			receiver = new ClientReceiver(socket,gui);						
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();	
		}catch(Exception e){
			return false;
		}	
		return true;
	}
	//---------------------------------------------Change look and feel
	private void changeLookAndFeel(){
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());	            
		            break;
		        }
		    }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//---------------------------------------------Create Login GUI
	private void createPasswordLoginGUI(){		
		//Set up panels
		JPanel rightPanel = new JPanel();	
		JPanel leftPanel = new JPanel();			
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		//Set up Frame
		frame = new JFrame("Chatter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());						
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon(this.getClass().getResource("image/icon.png")).getImage());
		
		//setup components
		password = new JPasswordField(20);
		username = new JTextField(20);		
		login = new JButton("   Login       ");
		register = new JButton("   Register  ");
		JCheckBox stayLoggedIn = new JCheckBox("Stay logged in  ");
		JTextPane serverStatus = new JTextPane();			
		
		//add action listeners			
		username.setActionCommand("login");
		password.setActionCommand("login");
		login.setActionCommand("login");
		register.setActionCommand("register");
		
		username.addActionListener(this);
		password.addActionListener(this);
		login.addActionListener(this);
		register.addActionListener(this);
		
		//Adding components to panels		
		leftPanel.setBorder(BorderFactory.createTitledBorder("Login Details: "));
		leftPanel.add(new JLabel("Username: "));
		leftPanel.add(username);		
		leftPanel.add(new JLabel("Password: "));
		leftPanel.add(password);
					
		rightPanel.add(new JLabel(" "));
		rightPanel.add(login);
		rightPanel.add(register);
		rightPanel.add(stayLoggedIn);
		rightPanel.add(new JLabel("     "));				

		frame.add(leftPanel, BorderLayout.WEST);	
		frame.add(rightPanel, BorderLayout.EAST);		

		
		frame.pack();
		frame.setVisible(true);
				
	}	
		

	
}
