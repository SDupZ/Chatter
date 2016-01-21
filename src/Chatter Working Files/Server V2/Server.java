import java.net.*;
import java.util.*;
import java.io.*;

public class Server implements Runnable {
    
    private ServerSocket serverSocket;
    private HashMap clientHashMap;
    private HashMap allUsers;
    private HashMap userInfo;
    private Database database;
    private AJPanel panel;
    
    public Server(AJPanel panel) {
        try {
            this.panel = panel;
            allUsers = new HashMap();
            database = new Database(this);
            allUsers = database.setAllUsers();
            clientHashMap = new HashMap();
            userInfo = new HashMap();
            serverSocket = new ServerSocket(6677);
        } catch (IOException e) {
            StackTraceElement[] elements = e.getStackTrace();
            for (int i = 0; i < elements.length; i++) {
                print(elements[i].toString());
            }
        }
    }
    
    public void run() {
        int idCounter = 1000000;
        while(true) {
            Socket tempSocket = null;
            try {
                tempSocket = serverSocket.accept();
                VClient newClient = new VClient(tempSocket, this, idCounter);
                idCounter = idCounter + idCounter / 5 - idCounter / 6;
                clientHashMap.put(newClient.getID(), newClient);
            } catch (Exception e) {
                StackTraceElement[] elements = e.getStackTrace();
                for (int i = 0; i < elements.length; i++) {
                    print(elements[i].toString());
                }
                System.exit(1);
            }
        }   
    }
    
    public void updateAllUsers(String username, int id) {
        allUsers.remove(username);
        allUsers.put(username, id);
    }
    
    public void sendAll(Package pack) {
        for (int j = 0; j < pack.getContacts().length; j++) {
            if (clientHashMap.get(pack.getContacts()[j].getID()) != null) {
                ((VClient)clientHashMap.get(pack.getContacts()[j].getID())).send(pack);
            }
        }
    }
    
    public void delete(int id) {
        VClient client = (VClient)clientHashMap.remove(id);
        panel.printInfo(client.toString() + " has disconnected");
        client.setHasLoggedIn(false);
        sendNewContact(client);
        client.setID(-1);
        client = null;
    }
    
    public boolean checkLoginInfo(String info) {
        return database.searchForLogin(info);
    }
    
    public boolean checkForDuplicate(String info) {
        Scanner scan = new Scanner(info);
        String username = scan.nextLine();
        Set clientSet = clientHashMap.entrySet();
        Iterator setI = clientSet.iterator();
        int i = 0;
        while(setI.hasNext()) {
            VClient tempClient = (VClient)((Map.Entry)setI.next()).getValue();
            if (tempClient.getUsername() != null && tempClient.getUsername().equals(username)) {
                scan.close();
                return true;
            }
        }
        scan.close();
        return false;
    }
    
    public boolean register(String info) {
        return database.registerInfo(info);
    }
    
    public void addContact(VClient client, String username) {
        if (allUsers.get(username) != null && (Integer)allUsers.get(username) != -1) {
            Contact[] contact = new Contact[1];
            contact[0] = new Contact(username, ((Integer)allUsers.get(username)).intValue(), Contact.NOT_CALLED);
            sendAll(new Package(Package.ADD_CONTACT, contact, new Contact(client.getUsername(), client.getID(), Contact.NOT_CALLED), null));
        } else if (allUsers.get(username) == null) {
            client.send(new Package(Package.ADD_CONTACT, (new String("invalidContact")).getBytes()));
        } else if ((Integer)allUsers.get(username) == -1) {
            Contact[] contacts;
            if (userInfo.get(client.getUsername()) != null) {
                contacts = new Contact[((Contact[])userInfo.get(client.getUsername())).length + 1];
                for (int i = 0; i < ((Contact[])userInfo.get(client.getUsername())).length; i++) {
                    contacts[i] = ((Contact[])userInfo.get(client.getUsername()))[i];
                }
            } else {
                contacts = new Contact[1];
            }
            contacts[contacts.length - 1] = new Contact(username, -1, Contact.NOT_CALLED);
            userInfo.put(username, contacts);
        }     
    }
    
    public void setContact(String username, String contact) {
        database.addContact(username, contact);
    }
    
    public Package contactList(VClient client) {
        String username = client.getUsername();
        int numberRegistered = database.getNumberRegistered(username + ".txt");
        Contact[] contacts = new Contact[numberRegistered];
        int j = 0;
        String userDetail;
        for (int i = 0; i < numberRegistered; i++) {
            if (!(userDetail = database.getUser(username + ".txt", i)).equals(client.getUsername())) {
                if ((Integer)allUsers.get(userDetail) != -1) {
                    contacts[j] = new Contact(userDetail, -1, Contact.NOT_CALLED);
                } else {
                    contacts[j] = new Contact(userDetail, -1, Contact.OFFLINE);
                }
                j++;
                client.updateFriends(userDetail);
            }
        }
        return new Package(Package.CONTACTS, contacts, new byte[0]);
    }
    
    /*public Package contactListOnline(VClient client) {
        Contact[] contacts = new Contact[clientHashMap.size() - 1];
        Set clientSet = clientHashMap.entrySet();
        Iterator setI = clientSet.iterator();
        int i = 0;
        while(setI.hasNext()) {
            VClient tempClient = (VClient)((Map.Entry)setI.next()).getValue();
            if (tempClient.getID() != client.getID()) {
                contacts[i] = new Contact(tempClient.getUsername(), tempClient.getID(), Contact.NOT_CALLED);
                i++;
            }
        }
        return new Package(Package.CONTACTS, contacts, new byte[0]);
    }*/
    
    public void sendNewContactRequests(VClient client) {
        for (int i = 0; i < ((Contact[])userInfo.get(client.getUsername())).length; i++) {
            Contact[] contact = new Contact[1];
            if ((Integer)(allUsers.get(((Contact[])userInfo.get(client.getUsername()))[i].getUsername())) != -1) {
                contact[0] = new Contact(((Contact[])userInfo.get(client.getUsername()))[i].getUsername(), (Integer)allUsers.get(((Contact[])userInfo.get(client.getUsername()))[i].getUsername()), Contact.NOT_CALLED);          
            } else {
                contact[0] = new Contact(((Contact[])userInfo.get(client.getUsername()))[i].getUsername(), -1, Contact.OFFLINE);
            }
            client.send(new Package(Package.CONTACTS, contact, null));
        }
    }
    
    public int getID(String username) {
        return ((Integer)allUsers.get(username)).intValue();
    }
    
    public void sendNewContact(VClient client) {
        Contact[] contact = new Contact[1];
        Package pack = new Package(Package.CONTACTS, contact, new byte[0]);
        Set clientSet = clientHashMap.entrySet();
        Iterator setI = clientSet.iterator();
        int i = 0;
        if (!client.getHasLoggedIn()) {
            int newId = client.getID();
            contact[0] = new Contact(client.getUsername(), -1, Contact.OFFLINE);
            allUsers.remove(client.getUsername());
            allUsers.put(client.getUsername(), -1);
            while(setI.hasNext()) {
                VClient tempClient = (VClient)((Map.Entry)setI.next()).getValue();
                if (client != tempClient) {
                    for (int j = 0; j < client.getFriends().size(); j++) {
                        if (client.getFriends().get(j).equals(tempClient.getUsername())) {
                            tempClient.send(pack); 
                            tempClient.removeFromCallGroup(newId);
                        }
                    }
                }
                i++;
            }
        } else {
            contact[0] = new Contact(client.getUsername(), client.getID(), Contact.NOT_CALLED);
            while(setI.hasNext()) {
                VClient tempClient = (VClient)((Map.Entry)setI.next()).getValue();
                if (client != tempClient) {
                    for (int j = 0; j < client.getFriends().size(); j++) {
                        if (client.getFriends().get(j).equals(tempClient.getUsername())) {
                            tempClient.send(pack); 
                        }
                    }
                }
                i++;
            }
        }
    }
    
    public void query() {
        panel.printInfo("" + clientHashMap.size());
        Set clientSet = clientHashMap.entrySet();
        Iterator setI = clientSet.iterator();
        int i = 0;
        while(setI.hasNext()) {
            panel.printInfo(((VClient)((Map.Entry)setI.next()).getValue()).toString());
            i++;
        }
    }
    
    public void print(String toScreen) {
        panel.printInfo(toScreen);
    }
}