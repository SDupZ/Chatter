/* Author: Simon du Preez 
* Date: 2012.08.22
*
* Version History:
*  1.00 - Created Prsogram
*/ 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath.*;

import javax.swing.UIManager.*;
	
public class ClientGUI extends JFrame implements TreeSelectionListener, ActionListener{
	private JFrame frame;
	private JTree contactsTree; 
	private VCTreeNode topLevel;
	private JSplitPane splitWindow; 
	private JPanel leftPanel;
	private JPanel rightCallPanel;
	private JPanel rightHomePanel; 
	private JPanel rightInCallPanel;
	
	private JMenuBar panelMenuBar;
	private JMenuBar inCallPanelMenuBar;
	private JButton call;
	private JButton drop;
	private JButton share;
	private Sender sender;
	
	private boolean verifiedByServer; 	
	private boolean failedLogin;
	private boolean callHasStarted;
	private boolean treeInitialised;
	private boolean duplicateLogin;
	private int registered;
	
	VoiceRecorder vRec;
	
	private ArrayList<Contact> contactsList = new ArrayList<Contact>();
	private Contact[] peopleInCall;
	
	JTextArea callInfo;
	
	//Constructer - to make gui.
	public ClientGUI(){
		registered = 0;
		duplicateLogin = false;
		callHasStarted = false;;
		verifiedByServer = false; 
		failedLogin = false; 
		treeInitialised = false;
	}
	//****************************************************************************************************
	//***Accessor and Mutator Methods
	//****************************************************************************************************
	public void setRegistered(int registered){
		this.registered = registered;
	}
	public int getRegistered(){
		return registered;
	}
	public boolean getDuplicateLogin(){
		return duplicateLogin;
	}
	public void setDuplicateLogin(boolean duplicateLogin){
		this.duplicateLogin = duplicateLogin;
	}
	public void setVerifiedByServer(boolean verifiedByServer){
		this.verifiedByServer = verifiedByServer;
	}
	public boolean isVerifiedByServer(){
		return verifiedByServer;
	}
	public void setContactsList(Contact[] contacts){
		for(int i = 0; i<contacts.length; i++){
			contactsList.add(contacts[i]);
		}	
	}
	public void setfailedLogin(boolean login){
		failedLogin = login;
	}
	public boolean getFailedLogin(){
		return failedLogin;
	}
	public void hasAcceptedCall(Contact personAccepted){		
		for(int i = 0; i< peopleInCall.length;i++){
			if(peopleInCall[i].getID() == (personAccepted.getID())){				
				peopleInCall[i] = personAccepted;
			}
		}
		callInfo.append("(ACCEPTED) " + personAccepted.getUsername());		
	}
	public void hasDeclinedCall(Contact personDeclined){
		for(int i = 0; i< peopleInCall.length;i++){
			if(peopleInCall[i].getID() == (personDeclined.getID())){				
				peopleInCall[i] = null;
			}
		}
		callInfo.append("(DECLINED) " + personDeclined.getUsername());
	}
	public boolean getCallHasStarted(){
		return callHasStarted;
	}
	public void setCallHasStarted(boolean callHasStarted){
		this.callHasStarted = callHasStarted;
		
		if(callHasStarted){		
			changeToInCallMenu();
						
			vRec = new VoiceRecorder(sender);
			vRec.setInCall(true); 
    		Thread vRecThread = new Thread(vRec);
    		vRecThread.start(); 
			//new Thread(new MusicStreamer(sender)).start();
		}
	}
	//****************************************************************************************************
	//Tree and ActionListeners
	//**************************************************************************************************** 
	//---------------------------------------------tree listener
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] selected = contactsTree.getSelectionPaths();	
		if(!callHasStarted && selected != null && !contactsTree.getPathForRow(0).getLastPathComponent().equals(selected[0].getLastPathComponent())){ 
			if(splitWindow.getComponent(2).equals(rightHomePanel)){
				splitWindow.remove(rightHomePanel);
				splitWindow.add(rightCallPanel);
				
			}    
		}else if(!callHasStarted){
			if(splitWindow.getComponent(2).equals(rightCallPanel)){   
				splitWindow.remove(rightCallPanel);
				splitWindow.add(rightHomePanel); 
			}
		}else if(callHasStarted){
		}
	}
	//---------------------------------------------Action Listener
	public void actionPerformed(ActionEvent e){		
		//~~~~~~~~~~~~~~~~~~~~~~~Call Button pressed
		if(e.getActionCommand().equals("CALL")){	
			Contact[] selectedContacts = getSelectedContacts();		
			sender.forward(new Package(Package.REQUEST, selectedContacts, new byte[0]));
			System.out.println("Call Request Sent");		
			call.setEnabled(false);
		//~~~~~~~~~~~~~~~~~~~~~~~Share Button Pressed
		}else if(e.getActionCommand().equals("SHARE")){
			//sender.forward(new Package(Package.MUSIC_REQUEST, peopleInCall, getAudioFormatByteArray()));
			//new Thread(new MusicStreamer(sender)).start();
		//~~~~~~~~~~~~~~~~~~~~~~~Drop Button PRessed		
		}else if(e.getActionCommand().equals("DROP")){
			vRec.setInCall(false);
			sender.forward(new Package(Package.DECLINED,null));
		}		
	} 
	//****************************************************************************************************
	//***Helper methods
	//****************************************************************************************************
	//---------------------------------------------Returns a byte array containting 
	public byte[] getAudioFormatByteArray(){
		byte[] audioByteFormat = new byte[7];
		
		//0 = ALAW, 1 = PCM_SIGNED, 2 = PCM_UNSIGNED, 3 = ULAW; 
		Integer encoding = 	new Integer(1);
		Float sampleRate = new Float(44100.0f);
		Integer sampleSizeInBits = new Integer(16);
		Integer channels = new Integer(2);
		Integer frameSize = new Integer(4);
		Float frameRate = new Float(44100.0f);
		Integer bigEndian = new Integer(0);
		
		audioByteFormat[0] = encoding.byteValue();
		audioByteFormat[1] = sampleRate.byteValue();
		audioByteFormat[2] = sampleSizeInBits.byteValue();
		audioByteFormat[3] = channels.byteValue();
		audioByteFormat[4] = frameSize.byteValue();
		audioByteFormat[5] = frameRate.byteValue();
		audioByteFormat[6] = bigEndian.byteValue();
		
		return audioByteFormat;
	}
	//---------------------------------------------Returns an array of contacts
	public Contact[] getSelectedContacts(){
		TreePath[] selected = contactsTree.getSelectionPaths();			
		Contact[] callContacts = new Contact[selected.length];			
		peopleInCall = new Contact[selected.length];
		
		for (int i = 0; i<selected.length;i++){
			callContacts[i] = ((VCTreeNode)selected[i].getLastPathComponent()).getContact();	
			peopleInCall[i] = callContacts[i];					
		}
		return callContacts;
	}
	//---------------------------------------------Accepts contact request
	public void acceptContact(Contact[] contacts, Contact contact, byte[] info){
		sender.forward(new Package(Package.ACCEPT_CONTACT, contacts, contact, info));
	}
	//---------------------------------------------Adds new contact
	public void addContact(Contact newContact){
		contactsList.add(newContact);		
		VCTreeNode newNode = new VCTreeNode(newContact);
		topLevel.add(newNode);
		
		updateContactTreeUI();
	}
	//---------------------------------------------Updates contacts list
	//new Contacts only ever contains a single contact.
	public void updateContactsList(Contact[] newContacts){							
		Contact updatedContact = newContacts[0];

		for(int i = 0; i<contactsList.size(); i++){	
			if(contactsList.get(i).getUsername().equals(updatedContact.getUsername())){
				contactsList.remove(i);	
				contactsList.add(i, updatedContact);
			}
		}
		if(treeInitialised == true){				
			//Contact logging in
			if(updatedContact.getInCall() != Contact.OFFLINE){
				VCTreeNode newNode = new VCTreeNode(updatedContact);
				topLevel.add(newNode);			
			}else{
			//contact logged out					
				for(int i = 0; i<topLevel.getChildCount(); i++){
					VCTreeNode node = (VCTreeNode)topLevel.getChildAt(i);
					if(node.getContact().getUsername().equals(updatedContact.getUsername())){	
						((DefaultTreeModel)contactsTree.getModel()).removeNodeFromParent(node);												
						break;
					}
				}		
			}
			updateContactTreeUI();				
		}							
	}
	//---------------------------------------------update the contacts tree ui
	public void updateContactTreeUI(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				((DefaultTreeModel)(contactsTree.getModel())).reload();	
			}
		});	
	}
	//---------------------------------------------change gui when calling
	private void callPersonGUI(){ 
		call.setEnabled(false);
		changeToInCallMenu();
	}	
	private void changeToInCallMenu(){
		if(splitWindow.getComponent(2).equals(rightHomePanel)){   
			splitWindow.remove(rightHomePanel);
			splitWindow.add(rightInCallPanel);
		}else{
			if(splitWindow.getComponent(2).equals(rightCallPanel)){   
				splitWindow.remove(rightCallPanel);
				splitWindow.add(rightInCallPanel); 
			}
		}   
	}
	//---------------------------------------------new call from somebody else
	//caller = person who called. pendingPeopleInCall = all people in call including me but not including the caller.	
	public void incomingCall(final Contact caller, final Contact[] pendingPeopleInCall){
		final JDialog incomingCallWindow = new JDialog();		
		JButton accept = new JButton("Accept");
		JButton decline = new JButton("Decline");
		callInfo = new JTextArea(6, 10);
		
		peopleInCall = new Contact[(pendingPeopleInCall.length)+1];
		peopleInCall[0] = caller;
		
		for(int i = 1; i< peopleInCall.length; i++){
			peopleInCall[i] = pendingPeopleInCall[(i-1)];
		}		
		for(int i = 0; i <peopleInCall.length; i++){
			switch(peopleInCall[i].getInCall()){
				case(Contact.ACCEPTED):
					callInfo.append("(ACCEPTED) " + peopleInCall[i].getUsername() + "\n");
					break;
				case(Contact.DECLINED):
					callInfo.append("(DECLINED) " + peopleInCall[i].getUsername()+ "\n");
					break;
				case(Contact.PENDING):
					callInfo.append("(PENDING) " + peopleInCall[i].getUsername()+ "\n");
				
			}			
		}		
		accept.addActionListener(new ActionListener(){			
			public void actionPerformed(ActionEvent e){   
				sender.forward(new Package(Package.ACCEPTED, pendingPeopleInCall, caller, null));
				incomingCallWindow.dispose();
				callPersonGUI();
				
				//new Thread(new MusicStreamer(sender)).start();				
				vRec = new VoiceRecorder(sender);
				vRec.setInCall(true);
    			Thread vRecThread = new Thread(vRec);
    			vRecThread.start();    			
			}
		});  
		decline.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sender.forward(new Package(Package.DECLINED, pendingPeopleInCall, caller, null));
				incomingCallWindow.dispose();
				call.setEnabled(true);
			}
		});
		incomingCallWindow.setTitle("Incoming call");
		incomingCallWindow.setLayout(new BorderLayout());
		incomingCallWindow.add(accept, BorderLayout.CENTER);
		incomingCallWindow.add(decline, BorderLayout.EAST);
		incomingCallWindow.add(callInfo, BorderLayout.WEST); 
		incomingCallWindow.setLocationRelativeTo(frame);
		incomingCallWindow.pack();
		incomingCallWindow.setVisible(true);   
	}
	//---------------------------------------------change look and fell
	private void changeLookAndFeel(){
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					UIManager.put("nimbusbase", new Color(28, 255, 23));
					break;
				}
			}		
		}catch (Exception e) {
			e.printStackTrace();
		}
	} 
		
	//---------------------------------------------create contacts tree
	private JTree createContactTree(){
		topLevel = new VCTreeNode("All Contacts");
		contactsTree = new JTree(topLevel);	

		for(int i = 0; i<contactsList.size(); i++){
			//IsOnline or not
			if(contactsList.get(i).getInCall() != Contact.OFFLINE){
				VCTreeNode currentNode = new VCTreeNode(contactsList.get(i));
				topLevel.add(currentNode);
			}
		}

		contactsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		contactsTree.addTreeSelectionListener(this);
		contactsTree.expandPath(contactsTree.getPathForRow(0));
		
		treeInitialised = true;
		updateContactTreeUI();
		return contactsTree;
	}
	//---------------------------------------------create buttons gui helper
	public void createButton(JButton myButton){
		myButton.setBorderPainted(false);
		myButton.setContentAreaFilled(false);
		myButton.setFocusable(false);		
		myButton.addActionListener(this);
	}
	//---------------------------------------------create whole gui helper
	public void createGUI(Sender infoSender) {
		sender = infoSender;  
		changeLookAndFeel();
	
		//~~~~~~~~~~~~~~~~~~~~~~~Creating frame
		frame = new JFrame("Chatter 2013");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setLayout(new BorderLayout());
		
		//~~~~~~~~~~~~~~~~~~~~~~~Create the Menu Bar
		JMenuBar mainMenuBar = new JMenuBar();
		JMenu optionsMenu = new JMenu("Options");
		JMenuItem addContact = new JMenuItem("+  Add Contact");
		JMenuItem about = new JMenuItem("About");
		JMenuItem quit = new JMenuItem(":( Quit");
		
		
		addContact.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				final JDialog addContactdialog = new JDialog(frame);
				final JTextField contactName = new JTextField();
								
				contactName.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						sender.forward(new Package(Package.ADD_CONTACT, contactName.getText().getBytes()));
						contactName.setText("");
						addContactdialog.dispose();
					}
				});
				
				addContactdialog.setLocationRelativeTo(frame);
				addContactdialog.add(contactName);	
				addContactdialog.pack();
				addContactdialog.setVisible(true);			
			}
		});
		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new JOptionPane().showMessageDialog(frame, "Chatter 2013 by Thommy + Simon. \nGraphics by Andrew Xu.");
			}
		});
		
		optionsMenu.add(addContact);
		optionsMenu.addSeparator();
		optionsMenu.add(quit); 
		
		mainMenuBar.add(optionsMenu);
		mainMenuBar.add(about);
		frame.setJMenuBar(mainMenuBar);
		
		splitWindow = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		//~~~~~~~~~~~~~~~~~~~~~~~Creating left panel
		leftPanel = new JPanel(); 
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		
		//Creating contact list tree.
		contactsTree = createContactTree();
		leftPanel.add(new JScrollPane(contactsTree));
		
		//~~~~~~~~~~~~~~~~~~~~~~~Creating right home panel call
		rightHomePanel = new JPanel();  
		rightHomePanel.setLayout(new BoxLayout(rightHomePanel, BoxLayout.Y_AXIS));
		
		rightHomePanel.setBackground(Color.WHITE);
		rightHomePanel.add(new JLabel(new ImageIcon(this.getClass().getResource("image/logo.png"))));		
		
		//~~~~~~~~~~~~~~~~~~~~~~~Creating right call panel call
		rightCallPanel = new JPanel();  
		rightCallPanel.setLayout(new BorderLayout());
		rightCallPanel.setBackground(Color.WHITE);
		//Adding menu to right panel
		panelMenuBar = new JMenuBar();
		
		call = new JButton(new ImageIcon(this.getClass().getResource("image/callButton.PNG")));		
		call.setRolloverIcon(new ImageIcon(this.getClass().getResource("image/callButtonRollOver.PNG"))); 
		call.setPressedIcon(new ImageIcon(this.getClass().getResource("image/callButtonPressed.PNG")));			
		createButton(call);
		call.setActionCommand("CALL");	
		
		panelMenuBar.add(call);
		//panelMenuBar.add(new JLabel(new ImageIcon(this.getClass().getResource("image/callIndicator.gif"))));  	
		rightCallPanel.add(panelMenuBar, BorderLayout.SOUTH);
		
		//~~~~~~~~~~~~~~~~~~~~~~~Creating right in call Panel
		rightInCallPanel = new JPanel();
		rightInCallPanel.setLayout(new BorderLayout());	
		rightInCallPanel.setBackground(Color.WHITE);
		
		inCallPanelMenuBar = new JMenuBar();			
		
		drop = new JButton(new ImageIcon(this.getClass().getResource("image/dropButton.PNG")));		
		drop.setRolloverIcon(new ImageIcon(this.getClass().getResource("image/dropButtonRollOver.PNG"))); 
		drop.setPressedIcon(new ImageIcon(this.getClass().getResource("image/dropButtonPressed.PNG")));
		createButton(drop);
		drop.setActionCommand("DROP");
		
		share = new JButton(new ImageIcon(this.getClass().getResource("image/shareButton.PNG")));			
		share.setRolloverIcon(new ImageIcon(this.getClass().getResource("image/shareButtonRollOver.PNG")));			
		share.setPressedIcon(new ImageIcon(this.getClass().getResource("image/shareButtonPressed.PNG")));			
		createButton(share);
		share.setActionCommand("SHARE");
		
		inCallPanelMenuBar.add(drop);
		inCallPanelMenuBar.add(share);  
		
		rightInCallPanel.add(inCallPanelMenuBar, BorderLayout.SOUTH); 
		
		//~~~~~~~~~~~~~~~~~~~~~~~Adding splitWindow to frame
		//Start with homepanel
		splitWindow.add(leftPanel);
		splitWindow.add(rightHomePanel);
		//splitWindow.add(rightCallPanel);
		
		frame.add(splitWindow);
		frame.setIconImage(new ImageIcon("C:/Users/Simon/Documents/Simon's Documents/Programming/Java Projects/Voice and Text Chat/Voice Chat App/image/icon.png").getImage());
		
		frame.pack();
		frame.setVisible(true);
	}
}
