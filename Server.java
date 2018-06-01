import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server extends JFrame{

    private Comms commLayer;
    private ArrayList<Item> items;
    private ArrayList<User> users;
    private ArrayList<Bid> bids;
    private DataPersistence dp;
    private DefaultListModel<String> clients;

    public void addClient(String client){
        clients.addElement(client);
    }

    public void removeClient(String client){
        clients.removeElement(client);
    }


    //create the server
    public Server(String title){
        super(title);
        //create the data persistence class, nameing the four files
        dp = new DataPersistence("items.data","users.data","bids.data","log.txt");
        //try to load the items, users and bids. If they don't load, initialise them as an empty array list
        try{
        items = dp.loadItemsFromFile();

        }
        catch (Exception e){
            items = new ArrayList<>();
        }
        try {
            users = dp.loadUsersFromFile();
        }
        catch (Exception e){
            users = new ArrayList<>();
        }
        try {
            bids = dp.loadBidsFromFile();
        }
        catch (Exception e){
            bids = new ArrayList<>();
        }
        //start the model of the list of clients
        clients = new DefaultListModel<>();
    }

    public synchronized void backUpData(){
        //use the data persistence layer to back up the data
        dp.writeUsersToFile(users);
        dp.writeBidsToFile(bids);
        dp.writeItemsToFile(items);
    }

    public void init(){
        //create the comm layer, and set it up for a server
        commLayer = new Comms(4141,null);
        commLayer.setUpForServer(this);
        //create and start the threads for running backup and scanning for auctions closing
        Thread backUpThread = new Thread(new BackUpThread(this));
        Thread scanThread = new Thread(new ScanThread(this));
        backUpThread.start();
        scanThread.start();
        //initialise the GU
        initGUI();

    }

    //getters for the various fields
    public synchronized ArrayList<Item> getItems(){
        return items;
    }

    public synchronized ArrayList<User> getUsers(){
        return users;
    }

    public synchronized ArrayList<Bid> getBids(){
        return bids;
    }

    public void initGUI(){
        //set the size of the gui
        setResizable(false);
        setSize(200,300);
        //tell the default close operation to do nothing when the window closes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //create a custom, window close operation, that backs up the data when the window closes
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //back up data, then exit the program
                backUpData();
                System.exit(0);
            }
        };
        //add the listener to the window
        addWindowListener(exitListener);
        setLayout(new FlowLayout());
        //add a JList of clients' IP addresses to the server's GUI
        JList<String> clientJList = new JList<>(clients);
        add(clientJList);
        JButton showSales = new JButton("Show Sales");
        //add the show sales button
        add(showSales);
        //and add it's listener
        showSales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //get the lock for System.out, to prevent the log from interrupting
                synchronized (System.out){
                    //go through the users
                    for (User u:users){
                        // print out their name, and the words has won
                        System.out.print(u.getName()+" has won: ");
                        //go through the items
                        for (Item i:items){
                            //print out the names of the items where their winner's ID matches the user's ID
                            if (i.getWinningUser()==u.getUserID()){
                                System.out.print(i.getItemTitle()+", ");
                            }
                        }
                        //end the line
                        System.out.println();
                    }
                }
            }
        });
        setVisible(true);
    }

    public Message dealWithMessage(Message m, String ip){
        //called by the various CommsThreads, for dealing with messages
        //log the message
        log(m,ip);
        Message reply=null;
        //check by message type watch the message contains, and call the appropriate method to deal with
        switch (m.getMessageType()){
            case Message.userRegistration: reply = registerUser(m);
                break;
            case Message.getUsers: reply = sendUsers();
                break;
            case Message.loginAuth: reply = loginUser(m);
                break;
            case Message.itemRegistration: reply = createItem(m);
                break;
            case Message.getBids: reply = sendBids();
                break;
            case Message.getItems: reply = sendItems();
                break;
            case Message.bidRegistration: reply = registerBid(m);
                break;
        }
        //if the reply isn't null, log the message.
        if (reply!=null){
        log(reply,ip);
        }
        //pass the reply back to the CommsThread, for them to send it to the client
        return reply;
    }

    public Message registerBid(Message m){
        //register a bid in the main system
        Bid b = (Bid) m.getAttachment();
        bids.add(b);
        return null;
    }

    public Message createItem(Message m){
        //add an item to the system
        //get the item from the attachment of the message
        Item i = (Item) m.getAttachment();
        //generate it's ID number
        i.generateItemID(items);
        //add it to the list of items
        items.add(i);
        //generate a reply to the message
        Message reply = new Message(Message.toClient,Message.itemRegistration);
        //set it's attachment as the item, now with ID number
        reply.setAttachment(i);
        //back up the newly modified data
        backUpData();
        return reply;
    }

    public Message sendUsers(){
        //create a new message
        Message reply = new Message(Message.toClient,Message.getUsers);
        //attach the list of all users to it
        reply.setAttachment(users);
        //return the message
        return reply;
    }

    public Message sendBids(){
        //attach the bids ArrayList to a message and send it to the client
        Message reply = new Message(Message.toClient,Message.getBids);
        reply.setAttachment(bids);
        return reply;
    }

    public Message registerUser(Message m){
        //get the new User from the message's attachment
        User newUser = (User) m.getAttachment();
        //generate it's id number
        newUser.generateUserID(users);
        //add it to the list
        users.add(newUser);
        //create a reply and attach the item with it's new ID number
        Message reply = new Message(Message.toClient,Message.userRegistration);
        reply.setAttachment(newUser);
        //back up the new data
        backUpData();
        //return the reply
        return reply;
    }



    public Message loginUser(Message m) {
        //create a reply
        Message toSend = new Message(Message.toClient,Message.loginAuth);
        //get the auth from the attachment
        Auth a = (Auth) m.getAttachment();
        //find the user that can accept the auth
        for (User u:users) {
            if (u.checkAuth(a)) {
                //attach it to the reply
                toSend.setAttachment(u);
            }
        }
        //if no user matches the auth, no attachment will be set
        return toSend;
    }

    public void log(Message m, String ip){
        //log a message, and the reply
        //get the local date time for when the message was sent
        String toReturn = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)+"";
        //add after it to the direction the message was sent
        switch (m.getDirection()){
            case Message.toClient: toReturn = toReturn+" to:";
                break;
            case Message.toServer: toReturn = toReturn+" from:";
                break;
        }
        //add after that add the IP of the client that sent/ received the message
        toReturn = toReturn+" "+ip+" {Class:";
        //log the message type, to the string
        switch (m.getMessageType()){
            case Message.getItems: toReturn = toReturn+" Items Requested,";
                break;
            case Message.loginAuth: toReturn = toReturn+" Login,";
                break;
            case Message.itemRegistration: toReturn = toReturn+" Item Added,";
                break;
            case Message.userRegistration: toReturn = toReturn+" User Added,";
                break;
            case Message.bidRegistration: toReturn = toReturn+" Bid added,";
                break;
            case Message.getBids: toReturn = toReturn+" bids requested,";
                break;
            case Message.getUsers: toReturn = toReturn+" Users requested";
                break;
            default: toReturn = toReturn+" Unknown message class, probably did something";
                break;
        }
        //log the attachment of the message
        toReturn = toReturn+" Attachment: ";
        if (m.getAttachment() == null){
            toReturn = toReturn+"null}";
        }
        else{
            toReturn = toReturn+m.getAttachment().getClass().toGenericString()+"}";
        }
        //print out this new log line
        synchronized (System.out) {
            System.out.println(toReturn);
        }
        //and write it to the log file
        dp.writeLogToFile(toReturn);
    }

    public Message sendItems(){
        //attach the item a message and send it to the client
        Message reply = new Message(Message.toClient,Message.getItems);
        reply.setAttachment(items);
        return reply;
    }

    public static void main(String[] args){
        //main method for the server
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                    Server server = new Server("Server Side App");
                    server.init();
            }
        });
        }
}
