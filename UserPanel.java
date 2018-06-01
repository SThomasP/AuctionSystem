import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UserPanel extends JPanel {


    private User user;
    private JLabel email, IDno,name, myBids, itemsISold, itemsIWon;
    private JList<Item>  sold, won;
    private Client frame;
    private DefaultListModel<Item> soldModel, wonModel;
    private JList<String> bidsJList;
    private DefaultListModel<String> bidModel;

    public void setUser(User user, ArrayList<Item> items, ArrayList<Bid> bids){
        //change the user of the user panel
        this.user = user;
        setLabels();
        //change the models of the JLists
        soldModel = getItemsSoldByUser(items);
        wonModel = getItemsWonByUser(items);
        bidModel = getBidsMadeByUser(items,bids);
        //set their models
        sold.setModel(soldModel);
        sold.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        won.setModel(wonModel);
        won.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bidsJList.setModel(bidModel);
    }

    public void refresh(ArrayList<Item> items, ArrayList<Bid> bids){
        //reset the user with the updated items and bids
        setUser(user,items,bids);
    }

    //get the list model of bids made by the user
    public DefaultListModel<String> getBidsMadeByUser(ArrayList<Item> items, ArrayList<Bid> bids){
        DefaultListModel<String> toReturn = new DefaultListModel<>();
        HashMap<Integer,Item> itemHashMap = new HashMap<>();
        //set the items to a hash map based on their ID numbers
        for(Item i:items){
            itemHashMap.put(i.getItemID(),i);
        }
        //go through the bids looking for ones placed by the user
        for (Bid b:bids){
            //add a string to the JList of the bid amount and the item it was placed on
            if (b.getUserID() == user.getUserID()){
                toReturn.addElement(b.getFormattedBid()+" on "+itemHashMap.get(b.getItemID()));
            }
        }
        //return the list model
        return toReturn;
    }

    //get the list of items won by the user
    public DefaultListModel<Item>  getItemsWonByUser(ArrayList<Item> items){
        int idNo = user.getUserID();
        DefaultListModel<Item> toReturn =  new DefaultListModel<Item>();
        for (Item i:items){
            //go through the items, adding ones won by the user
            if (i.getWinningUser() == idNo){
                toReturn.addElement(i);
            }
        }
        return toReturn;
    }

    public DefaultListModel<Item>  getItemsSoldByUser(ArrayList<Item> items){
        //list of items, sold by the user
        int idNo = user.getUserID();
        DefaultListModel<Item> toReturn =  new DefaultListModel<Item>();
        //go through the items looking for ones sold by the user
        for (Item i:items){
            if (i.getSellersUserID() == idNo){
                toReturn.addElement(i);
            }
        }
        return toReturn;
    }

    private void setLabels(){
        //set the labels to the user's details
        email.setText("Email: "+user.getEmailAddress());
        name.setText("Name: "+user.getName());
        IDno.setText("ID no: "+Integer.toString(user.getUserID()));
    }

    public void init(){
        //create the components
        email = new JLabel();
        IDno = new JLabel();
        setBorder(BorderFactory.createLineBorder(Color.black));
        name = new JLabel();
        myBids = new JLabel("My Bids:");
        itemsISold = new JLabel("Items I've Sold:");
        itemsIWon = new JLabel("Items I've Won:");
        bidsJList = new JList<>();
        sold = new JList<>();
        //give both the items won and items sold lists selection listeners so that the panel can switch to the selected item
        sold.addListSelectionListener(new SwitchToItemListener(frame,sold));
        won = new JList<>();
        won.addListSelectionListener(new SwitchToItemListener(frame,won));
        //create a layout and constraints, and add the components to the panel
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(name,constraints);
        constraints.gridy = 1;
        add(email,constraints);
        constraints.gridy = 2;
        add(IDno,constraints);
        constraints.gridy = 3;
        add(myBids,constraints);
        constraints.gridx=1;
        add(itemsISold,constraints);
        constraints.gridx=2;
        add(itemsIWon,constraints);
        constraints.weightx =1;
        constraints.weighty =1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy =4;
        constraints.gridx =0;
        add( new JScrollPane(bidsJList),constraints);
        constraints.gridx=1;
        add( new JScrollPane(sold),constraints);
        constraints.gridx=2;
        add( new JScrollPane(won),constraints);
    }

    public UserPanel(Client frame){
        this.frame = frame;
    }
}
