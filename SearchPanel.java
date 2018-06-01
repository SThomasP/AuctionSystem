import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SearchPanel extends JPanel {

    private JList<Item> itemJList;
    private JComboBox<String> searchOptionsBox;
    private DefaultListModel<Item> itemListModel;
    private ArrayList<Item> allItems;
    private JButton searchButton, myButton, sellButton, refreshButton;
    private JTextField searchField;
    private Client frame;

    //final array list of strings, equals to the search options
    private static final String[] searchOptions = {"For Sale","By Seller's ID","By Category","By Item ID","Created After"};

    //init the search panel
    public void init(User user){
        //create an action listener, for searching
        ActionListener searchListener =  new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSearch();
            }
       };
        searchButton = new JButton("Search");
        //add the search listener, to the search button
        searchButton.addActionListener(searchListener);
        //create a button for the access of the logged in user's panel
        myButton = new JButton(user.getName());
        itemJList = new JList<>();
        //when an item on this JList is clicked, the frame switches the other panel for this one item
        itemJList.addListSelectionListener(new SwitchToItemListener(frame,itemJList){
            @Override
            //an extension, of the switch to item listener, which clear the list afterwards
            public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);
                clearListSelection();
            }
        });
        //adds the search listener to the search field as well, so that when enter is pressed it searches
        searchField = new JTextField();
        searchField.addActionListener(searchListener);
        //tell the frame to switch to the user's panel
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              frame.switchToUserPanel(user);
            }
        });
        searchOptionsBox = new JComboBox<>(searchOptions);
        sellButton = new JButton("Sell an Item");
        sellButton.addActionListener(new ActionListener() {
            @Override
            //create a button to allow the user to sell an item, showing the item registration panel
            public void actionPerformed(ActionEvent e) {
                frame.switchToItemRegistrationPanel();
            }
        });
        setBorder(BorderFactory.createLineBorder(Color.black));
        refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            //create a button to refresh the frame, and get the latest data from the server
            public void actionPerformed(ActionEvent e) {
                frame.refresh(false);
            }
        });
        //add everything to frame with a grid bag layout
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.weightx=0.3;
        constraints.weighty=0.1;
        add(myButton, constraints);
        constraints.gridx=1;
        add(refreshButton,constraints);
        constraints.gridx=2;
        add(sellButton,constraints);
        constraints.gridx=0;
        constraints.gridy=1;
        add(searchOptionsBox,constraints);
        constraints.gridx=1;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        add(searchField,constraints);
        constraints.fill=GridBagConstraints.NONE;
        constraints.gridx=2;
        add(searchButton,constraints);
        constraints.gridx=0;
        constraints.gridy=2;
        constraints.gridwidth=3;
        constraints.weighty=1.0;
        constraints.weightx=1.0;
        constraints.fill=GridBagConstraints.BOTH;
        add( new JScrollPane(itemJList),constraints);
        setVisible(true);

    }


    public DefaultListModel<Item> getItemsForSale(){
        //get the list of items for sale
        DefaultListModel<Item> toReturn = new DefaultListModel<>();
        for(Item i:allItems){
            if (!i.getClosed()){
                //if the auction is not closed
                toReturn.addElement(i);
            }
        }
        return toReturn;
    }

    public void clearListSelection(){
        itemJList.clearSelection();
    }

    public DefaultListModel<Item> getByItemID(){
        //search for item by it's item ID
        DefaultListModel<Item> toReturn = new DefaultListModel<>();
        for (Item i:allItems){
            try{
                if (i.getItemID()== Integer.valueOf(searchField.getText())){
                    toReturn.addElement(i);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return toReturn;
    }

    public DefaultListModel<Item> getItemsBySellersID(){
        //search for an item by the seller's ID number,
        DefaultListModel<Item> toReturn = new DefaultListModel<>();
        for(Item i:allItems){
            try {
                if (i.getSellersUserID() == Integer.valueOf(searchField.getText())) {
                    toReturn.addElement(i);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return toReturn;
    }

    public DefaultListModel<Item> getByCategory(){
        //search for items by their category
        DefaultListModel<Item> toReturn = new DefaultListModel<>();
        for(Item i:allItems){
            //if the item's category equals the search field's text
            if (i.getCategory().equals(searchField.getText())){
                toReturn.addElement(i);
            }
        }
        return toReturn;
    }

    public DefaultListModel<Item> getByDateCreated(){
        //searches for items after a date created
        DefaultListModel<Item> toReturn = new DefaultListModel<>();
        for (Item i:allItems){
            //checks to see if the auction started after the date it was created, by passing in the date in the source field of a certain pattern
            if (i.getTimeStarted().isAfter(LocalDateTime.parse(searchField.getText(),DateTimeFormatter.ofPattern("dd/MM/yyyy")))){
                toReturn.addElement(i);
            }
        }
        return toReturn;
    }

    public void runSearch(){
        //get the index of the search mode box and use this to figure out the search method
        switch (searchOptionsBox.getSelectedIndex()) {
            case 0:itemListModel = getItemsForSale();
                break;
            case 1:itemListModel =  getItemsBySellersID();
                break;
            case 2:itemListModel = getByCategory();
                break;
            case 3:itemListModel = getByItemID();
                break;
            case 4:itemListModel = getByDateCreated();
                break;
            default:itemListModel = new DefaultListModel<>();
                break;
        }
        itemJList.setModel(itemListModel);
    }

    public void refresh(ArrayList<Item> allItems){
        //upon refresh, update the list of items, then rerun the search to update  the list being shown
        this.allItems=allItems;
        runSearch();
    }

    public SearchPanel(Client frame){
        this.frame = frame;
    }
}
