import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class Client extends JFrame {

    private Comms commLayer;
    private ArrayList<Item> items;
    private ArrayList<Integer> itemsWon;
    private ArrayList<User> users;
    private ArrayList<Bid> bids;
    private ItemPanel itemPanel;
    private  JPanel cardPanel;
    private UserPanel userPanel;
    private SearchPanel searchPanel;
    private ItemRegistrationPanel itemRegistrationPanel;
    private User user;
    private String cardMemory;


    //String references for the three different panels
    private  static final String userCard = "U PANEL";
    private static final String itemCard = "I PANEL";
    private static final String itemRegistrationCard = " I R PANEL";

    public Client(String title){
        super(title);
        boolean connected = false;
        //can't leave the while loop until a connection is established
        while (!connected){
            //user Dialog asking for the server's address
            String serverName = JOptionPane.showInputDialog(null,"Enter name of server to connect to:");
            //exits the program, is
            if (serverName == null){
                System.exit(0);
            }
            //create a commLayer for clients
            commLayer = new Comms(4141,serverName);
            if(commLayer.setUpForClient()){
                connected = true;
            }
        }
    }

    public int getUsersID(){
        //get the id number of the logged in user
        return user.getUserID();
    }

    public void switchToUserPanel(User user){
        //sets the user panel to visible, and changes the user being shown
        userPanel.setUser(user,items, bids);
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel,userCard);
        cardMemory = userCard;
    }

    public void switchToItemRegistrationPanel(){
        //Clear the Item registration Panel, then switch it to the active panel
        itemRegistrationPanel.clear();
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel,itemRegistrationCard);
        cardMemory = itemRegistrationCard;
    }

    public void switchToItemPanel(Item item){
        //set the items panel's item to the argument, then make it the active panel
        itemPanel.changeItem(item,bids);
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel,itemCard);
        cardMemory = itemCard;
    }

    public void init(){
        //initiates the frame
        runUserLogin();
        setSize(1200,650);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        CardLayout cl = new CardLayout();
        //creates the panels
        cardPanel = new JPanel(cl);
        userPanel = new UserPanel(this);
        userPanel.init();
        itemPanel = new ItemPanel();
        itemRegistrationPanel = new ItemRegistrationPanel(this);
        itemRegistrationPanel.init();
        searchPanel = new SearchPanel(this);
        searchPanel.init(user);
        itemPanel.init(this);
        //adds the three panels and their references to the card panel
        cardPanel.add(userPanel, userCard);
        cardPanel.add(itemPanel, itemCard);
        cardPanel.add(itemRegistrationPanel, itemRegistrationCard);
        cl.show(cardPanel,userCard);
        cardMemory = userCard;
        //adds the card panel and the search panel to the frame
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx=1;
        constraints.weighty=1;
        constraints.fill = GridBagConstraints.BOTH;
        add(searchPanel,constraints);
        add(cardPanel,constraints);
        //runs the first of the refresh methods
        itemsWon = new ArrayList<>();
        refresh(true);
        //creates the thread to run routine refreshes
        Thread refreshThread = new Thread(new RefreshThread(this));
        refreshThread.start();
        //sets the frame to visible
        setVisible(true);
    }

    public synchronized void refresh(boolean firstTime){
        //update everything from the server
        updateItemsFromServer();
        updateUsersFromServer();
        updateBidsFromServer();
        //refresh the search panel
        searchPanel.refresh(items);
        if (firstTime) {
            //for the first time this is method run
            //set the default view, to the logged in user's page
            userPanel.setUser(user,items,bids);
            //look through the items for ones won by the user
            for (Item i: items){
                if (i.getWinningUser()==user.getUserID()){
                    itemsWon.add(i.getItemID());
                }
            }
        }
        else{
            //for later time the refresh method is running
            if (cardMemory.equals(userCard)){
                //if the user panel is visible
                userPanel.refresh(items,bids);
            }
            else if (cardMemory.equals( itemCard)){
                //if the item panel is visible
                itemPanel.refresh(bids,items);
            }
            for (Item i: items){
                //scan through the items, looking for ones won by the item
                if (i.getWinningUser()==user.getUserID()){
                    //checks if it has already been noted
                    if (!itemsWon.contains(i.getItemID())){
                        //show a congratulatory dialog
                        JOptionPane.showMessageDialog(this, "Congratulations, You're now the proud owner of "+i.getItemTitle()+"!");
                        //add it to the list
                        itemsWon.add(i.getItemID());
                    }
                }
            }
        }
    }

    public void updateUsersFromServer() {
        try {
            //send a request message to the server, requesting the list of all users
            Message requestMessage = new Message(Message.toServer, Message.getUsers);
            commLayer.sendMessage(requestMessage);
            //get the reply
            Message reply = commLayer.getMessage();
            users = (ArrayList<User>) reply.getAttachment();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public User getUserFromID(int userID){
        //searches through the list of users, to find the one with the matching ID number
        for (User u:users){
            if (u.getUserID()== userID){
                return u;
            }
        }
        return null;
    }

    public void updateBidsFromServer(){
        try {
            //send a message to request all bids
            Message requestMessage = new Message(Message.toServer, Message.getBids);
            commLayer.sendMessage(requestMessage);
            //get the reply from the server
            Message reply = commLayer.getMessage();
            bids = (ArrayList<Bid>) reply.getAttachment();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateItemsFromServer() {
        try {
            //send a message to request all items from the server
            Message requestMessage = new Message(Message.toServer, Message.getItems);
            commLayer.sendMessage(requestMessage);
            //get the reply from the server
            Message reply = commLayer.getMessage();
            //set the messages attachment to the items
            items = (ArrayList<Item>) reply.getAttachment();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void registerUser(String email, String password,String givenName, String familyName){
        try {
            //create a user
            User newUser = new User(givenName, familyName, email);
            //set their password
            newUser.setPassword(password);
            //send a message with the user as the attachment
            Message userReg = new Message(Message.toServer, Message.userRegistration);
            userReg.setAttachment(newUser);
            commLayer.sendMessage(userReg);
            //get the user back,complete with ID number given to it by the server
            Message reply = commLayer.getMessage();
            newUser = (User) reply.getAttachment();
            //log in as the new user
            user = newUser;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void placeBid(Item item, ArrayList<Bid> bidsOnItem){
        //check to make sure the user is not the seller
        if (item.getSellersUserID()==user.getUserID()){
            JOptionPane.showMessageDialog(this,"Can't place a bid on you own item");
        }
        else{
            //set the minimum amount you can bid to the items reserve price
            double minimum = item.getReservePrice();
            if (!bidsOnItem.isEmpty()){
                //if the bid list is not empty, set the minimum to max of the bigs
                minimum = Collections.max(bidsOnItem).getPrice();
            }
            //create a spinner to create the bid
            JSpinner bidSpinner = new JSpinner(new SpinnerNumberModel(minimum+1,minimum+0.01,100000.0,0.01));
            bidSpinner.setEditor(new JSpinner.NumberEditor(bidSpinner,"£0.00"));
            JComponent[] dialogComponents = {new JLabel("Bid Amount: "), bidSpinner};
            Object[] options = {"Place Bid","Cancel"};
            //show a message requesting of the amount of the bid
            int response = JOptionPane.showOptionDialog(this, dialogComponents,"Bid Placement",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
            if (response==JOptionPane.OK_OPTION){
                //if its the OK option, create the bid and send it to the server
                Message m = new Message(Message.toServer,Message.bidRegistration);
                Bid b = new Bid(user.getUserID(),item.getItemID(),(double) bidSpinner.getValue());
                m.setAttachment(b);
                commLayer.sendMessage(m);
                //refresh the client
                refresh(false);
                JOptionPane.showMessageDialog(this,"Bid Placed");
            }
            else{
                //if its cancelled or closed
                JOptionPane.showMessageDialog(this, "Bid Cancelled");
            }
        }
    }

    public void loginUser(String email, String password) {
        try {
            //create an auth for the email and the password
            Auth a = new Auth(email, password);
            //send it to the server
            Message userAuth = new Message(Message.toServer, Message.loginAuth);
            userAuth.setAttachment(a);
            commLayer.sendMessage(userAuth);
            //get the reply from the server, this should be the user to log in as
            Message reply = commLayer.getMessage();
            User toLoginAs = (User) reply.getAttachment();
            if (toLoginAs != null) {
                //if the user is not a null variable
                user = toLoginAs;
            }
            else {
                //if it is null variable, it's not a valid user
                JOptionPane.showMessageDialog(null, "Incorrect Login");
                //so run the login method again
                runUserLogin();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runUserLogin(){
        int response=0;
        boolean validText=false;
        String email= null;
        String password =null;
        JComponent[] loginComponents = {new JLabel("Email:"),new JTextField(),new JLabel("Password:"),new JPasswordField()};
        Object[] options = {"Login","Register","Cancel"};
        while (!(validText)&&((response!=JOptionPane.CANCEL_OPTION)&&response!=JOptionPane.CLOSED_OPTION)) {
            //show a login dialog
            response = JOptionPane.showOptionDialog(null, loginComponents, "Login", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            //get the email and the password from the dialog
            email = ((JTextField) loginComponents[1]).getText();
            password = String.copyValueOf(((JPasswordField) loginComponents[3]).getPassword());
            //check that both fields have text
            validText = (!email.isEmpty() && !password.isEmpty());
        }
        switch (response){
            //if the login button is clicked, log the user in
            case JOptionPane.YES_OPTION: loginUser(email,password);
                break;
            //if the register button is clicked, register the user
            case JOptionPane.NO_OPTION: getRegistrationInfo(email,password);
                break;
            //if any of the other buttons are clicked, exit the program
            case JOptionPane.CANCEL_OPTION: case JOptionPane.CLOSED_OPTION: System.exit(0);
                break;
        }
    }

    private void getRegistrationInfo(String email, String password){
        //obscures the password, showing only the first and last characters
        String passwordString = Character.toString(password.charAt(0));
        for (int i=0; i<password.length()-2;i++)
            passwordString=passwordString+"*";
        passwordString=passwordString+password.charAt(password.length()-1);
        //sets things up for a registration dialog
        JComponent[] loginComponents = {new JLabel("Email: "+email),new JLabel("Given Name:"),new JTextField(),new JLabel("Family Name:"),new JTextField(),new JLabel("Password: "+passwordString)};
        Object[] options = {"Register","Cancel"};
        String givenName =null;
        String familyName =null;
        boolean validText =false;
        int response = 0;
        while (!(validText)&&(response!=JOptionPane.CANCEL_OPTION)) {
            // shows the registration dialog
            response = JOptionPane.showOptionDialog(null, loginComponents, "Register", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            givenName = ((JTextField) loginComponents[2]).getText();
            familyName = ((JTextField) loginComponents[4]).getText();
            //check that both fields have text
            validText =(!givenName.isEmpty() && !familyName.isEmpty());
        }
        //register the user, once all information has been collected
        registerUser(email,password,givenName,familyName);
    }

    public void registerItem(Item item) {
        //send a message about the registration to the server
        try {
            Message regMess = new Message(Message.toServer, Message.itemRegistration);
            regMess.setAttachment(item);
            commLayer.sendMessage(regMess);
            //get a reply with message of the item with an ID number
            Message reply = commLayer.getMessage();
            Item itemWithID = (Item) reply.getAttachment();
            items.add(itemWithID);
            refresh(false);
            //switch the panel to the item panel of the newly created panel
            switchToItemPanel(itemWithID);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater((new Runnable() {
            public void run() {
                Client client = new Client("Online Auction Client");
                client.init();
            }
        }));
    }
}
