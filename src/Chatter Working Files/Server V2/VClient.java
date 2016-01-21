import java.util.*;
import java.net.*;
import java.io.*;

public class VClient {
    
    private Socket socket;
    private Server server;
    private Sender sender;
    private Receiver receiver;
    private String username;
    private int id;
    private boolean hasLoggedIn;
    private boolean inCall;
    private ArrayList<Integer> callGroupIds;
    private ArrayList<String> friends;
    
    public VClient(Socket socket, Server server, int id) {
        this.socket = socket;
        this.server = server;
        this.id = id;
        hasLoggedIn = false;
        //
        //player = new Playback();
        //
        callGroupIds = new ArrayList<Integer>();
        friends = new ArrayList<String>();
        sender = new Sender(socket, server);
        receiver = new Receiver(socket, this, server);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }
    
    public String getUsername() {
        return username;
    }
    
    public String toString() {
        return username + " " + socket.getInetAddress() + " " + id + " is in a call group " + (callGroupIds.size() != 0);
    }
    
    public int getID() {
        return id;
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public boolean getHasLoggedIn() {
        return hasLoggedIn;
    }
    
    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn = hasLoggedIn;
    }
    
    public void updateFriends(String newFriend) {
        friends.add(newFriend);
    }
    
    public ArrayList<String> getFriends() {
        return friends;
    }
    
    public void send(Package pack) {
        if (pack.getType() == Package.ACCEPTED) {
            callGroupIds.add(pack.getContact().getID());
        } else if (pack.getType() == Package.REQUEST && !inCall) {
            callGroupIds.clear();
        } else if (pack.getType() == Package.DECLINED) {
            for (int i = 0; i < callGroupIds.size(); i++) {
                if (pack.getContact().getID() == callGroupIds.get(i)) {
                    callGroupIds.remove(i);
                }
            }
        } else if (pack.getType() == Package.ACCEPT_CONTACT) {
            server.setContact(username, pack.getContact().getUsername());
            friends.add(pack.getContact().getUsername());
        }
        sender.forward(pack);
    }
    
    public void removeFromCallGroup(int newId) {
        for (int i = 0; i < callGroupIds.size(); i++) {
            if (callGroupIds.get(i) == newId) {
                callGroupIds.remove(i);
            }
        }
    }
    
    public void toServer(Package currentPack) {
        if (hasLoggedIn) {
            if (currentPack.getType() == Package.TEXT) {
                server.sendAll(currentPack);
                
                
                //Server Information Output Start
                server.print("User: " + username + " // IP: " + socket.getInetAddress() + "\n sent message: " + new String(currentPack.getInfo()) + " // to: ");
                for (int i = 0; i < currentPack.getContacts().length; i++) {
                    server.print(currentPack.getContacts()[i] + " ");
                }
                //Server Information Output End
                
                
            } else if (currentPack.getType() == Package.ACCEPTED) {
                inCall = true;
                Contact[] contacts = new Contact[currentPack.getContacts().length];
                int j = 0;
                for (int i = 0; i < currentPack.getContacts().length; i++) {
                    if (currentPack.getContacts()[i].getID() != id) {
                        contacts[j] = currentPack.getContacts()[i];
                        j++;
                    }
                }
                callGroupIds.add(currentPack.getContact().getID());
                if (contacts.length > 0) {
                    contacts[contacts.length - 1] = currentPack.getContact();
                }
                server.sendAll(new Package(Package.ACCEPTED, contacts, new Contact(username, id, Contact.ACCEPTED), currentPack.getInfo()));
                
                
                //Server Information Output Start
                server.print("User: " + username + " ID: " + id + " IP: " + socket.getInetAddress() + " accepted call");
                //Server Information Output End
                
                
            } else if (currentPack.getType() == Package.DECLINED) {
                Contact[] contacts;
                if (!inCall) {
                    contacts = new Contact[currentPack.getContacts().length];
                    int j = 0;
                    for (int i = 0; i < currentPack.getContacts().length; i++) {
                        if (currentPack.getContacts()[i].getID() != id) {;
                            contacts[j] = currentPack.getContacts()[i];
                            j++;
                        }
                    }
                    contacts[contacts.length - 1] = currentPack.getContact();
                } else {
                    inCall = false;
                    contacts = new Contact[callGroupIds.size()];
                    for (int i = 0; i < contacts.length; i++) {
                        contacts[i] = new Contact(null, callGroupIds.get(i), Contact.ACCEPTED);
                    }
                }
                callGroupIds.clear();
                server.sendAll(new Package(Package.DECLINED, contacts, new Contact(username, id, Contact.DECLINED), currentPack.getInfo()));
                
                
                //Server Information Output Start
                server.print("User: " + username + " ID: " + id + " IP: " + socket.getInetAddress() + " declined call");
                //Server Information Output End
                
                    
            } else if (currentPack.getType() == Package.REQUEST) {
                if (callGroupIds.size() == 0) {
                    System.out.println(currentPack.getContacts()[0].getUsername());
                    server.sendAll(new Package(currentPack.getType(), currentPack.getContacts(), new Contact(username, id, Contact.ACCEPTED), currentPack.getInfo()));
                    
                    
                    //Server Information Output Start
                    server.print("User: " + username + " // IP: " + socket.getInetAddress() + " // ID: " + id + "\nCalling Users: ");
                    for (int i = 0; i < currentPack.getContacts().length; i++) {
                        server.print(currentPack.getContacts()[i].getUsername() + " ");
                    }
                    server.print("\n");
                    //Server Information Output End
                    
                    
                } else {
                    server.print("User: " + username + " // IP: " + socket.getInetAddress() + "\nAdding User: " + currentPack.getContacts()[0].getUsername());
                }
            } else if (currentPack.getType() == Package.VOICE || currentPack.getType() == Package.MUSIC) {
                Contact[] contacts = new Contact[callGroupIds.size()];
                for (int i = 0; i < callGroupIds.size(); i++) {
                    contacts[i] = new Contact(null, callGroupIds.get(i), Contact.ACCEPTED);
                }
                System.out.println("LL");
                server.sendAll(new Package(currentPack.getType(), contacts, currentPack.getInfo()));
            } else if (currentPack.getType() == Package.MUSIC_REQUEST) {
                Contact[] contacts = new Contact[callGroupIds.size()];
                for (int i = 0; i < callGroupIds.size(); i++) {
                    contacts[i] = new Contact(null, callGroupIds.get(i), Contact.ACCEPTED);
                }
                server.sendAll(new Package(currentPack.getType(), contacts, new Contact(username, id, Contact.ACCEPTED), currentPack.getInfo()));
            } else if (currentPack.getType() == Package.FILE) {
                server.print("User: " + username + " // IP: " + socket.getInetAddress() + "\nSending file to Users: ");
                for (int i = 0; i < currentPack.getContacts().length; i++) {
                    server.print(currentPack.getContacts()[i].getUsername() + " ");
                }
                server.sendAll(currentPack);
            /*} else if (currentPack.getType() == Package.ADD_CONTACT) {
                if (server.checkForUser(currentPack.getContacts[0].getUsername())) {
                    server.addContact(currentPack.getContacts[0].getUsername());
                }*/
            } else if (currentPack.getType() == Package.ADD_CONTACT) {
                server.print("User: " + username + " // IP: " + socket.getInetAddress() + "\nAttempting to add contact: " + new String(currentPack.getInfo()));
                if (friends.size() > 0) {
                    for (int i = 0; i < friends.size(); i++) {
                        if (new String(currentPack.getInfo()).equals(friends.get(i))) {
                            send(new Package(Package.DECLINE_CONTACT, new String("existingContact").getBytes()));
                        } else {
                            Contact[] contact = new Contact[1];
                            contact[0] = new Contact(username, id, Contact.NOT_CALLED);
                            server.addContact(this, new String(currentPack.getInfo()));
                        }
                    }
                } else if (new String(currentPack.getInfo()).equals(username)) {
                    send(new Package(Package.DECLINE_CONTACT, new String("selfContact").getBytes()));
                } else {
                    Contact[] contact = new Contact[1];
                    contact[0] = new Contact(username, id, Contact.NOT_CALLED);
                    server.addContact(this, new String(currentPack.getInfo()));
                }
            } else if (currentPack.getType() == Package.ACCEPT_CONTACT) {
                server.print("User: " + username + " // IP: " + socket.getInetAddress() + "\nAccepted: " + currentPack.getContact().getUsername());
                server.setContact(username, currentPack.getContact().getUsername());
                friends.add(currentPack.getContact().getUsername());
                Contact[] contact = new Contact[1];
                contact[0] = new Contact(currentPack.getContact().getUsername(), server.getID(currentPack.getContact().getUsername()), Contact.NOT_CALLED);
                server.sendAll(new Package(Package.ACCEPT_CONTACT, contact, new Contact(username, id, Contact.NOT_CALLED), null));
            }
        } else {
            if (currentPack.getType() == Package.REGISTER) {
                if (server.register(new String(currentPack.getInfo()))) {
                    send(new Package(Package.TEXT, (new String("successfulRegistration")).getBytes()));
                } else {
                    send(new Package(Package.TEXT, (new String("failedRegistration")).getBytes()));
                }
            } else {
                hasLoggedIn = server.checkLoginInfo(new String(currentPack.getInfo())); 
                if (!hasLoggedIn) {
                    server.print("User has not logged in.");
                    send(new Package(Package.TEXT, (new String("failedLogin")).getBytes()));
                    destroy();
                } else if (hasLoggedIn && server.checkForDuplicate(new String(currentPack.getInfo()))) {
                    hasLoggedIn = false;
                    send(new Package(Package.TEXT, (new String("duplicateLogin")).getBytes()));
                    destroy();
                } else {
                    String info = new String(currentPack.getInfo());
                    username = info.substring(0, info.indexOf("\n"));
                    server.updateAllUsers(username, id);
                    server.print(username + " " + socket.getInetAddress() + " " + id + " has connected");
                    send(server.contactList(this));
                    //send(server.contactListOnline(this));
                    server.sendNewContact(this);
                    //send(new Package(Package.TEXT, (new String("successfulLogin")).getBytes()));
                }
            }
        }
    }
    
    public void destroy() {
        server.delete(id);
    }
}