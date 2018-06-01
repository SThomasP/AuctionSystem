import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ItemPanel extends JPanel {

    private Item item;
    private JList<Bid> bidsJList;
    private BidListModel bids;
    private JButton bidButton;
    private JLabel itemName;
    private JLabel itemCategory , bidLabel;
    private JTextArea itemDescription;
    private JLabel itemClosesAt;
    private JLabel itemClosed,itemID;
    private JLabel reservePrice;
    private JLabel soldBy, wonBy, descriptionLabel;
    private JLabel soldFor;
    private Client frame;


    public ItemPanel(){
        //initialise all the items in the panel
        item = null;
        soldFor = new JLabel();
        bidsJList = new JList<>();
        itemID = new JLabel();
        bidLabel = new JLabel("Bids already Placed:");
        bidButton = new JButton("Bid");
        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.placeBid(item,bids.getAllBids());
            }
        });
        itemName = new JLabel();
        soldBy = new JLabel("Sold By");
        wonBy = new JLabel("Won By");
        descriptionLabel = new JLabel("Item Description");
        itemCategory = new JLabel();
        itemDescription = new JTextArea();
        itemDescription.setLineWrap(true);
        itemDescription.setEditable(false);
        itemClosed = new JLabel();
        itemClosesAt = new JLabel();
        reservePrice = new JLabel();

    }

    public void init(Client frame){
        //add everything into the panel, and set the layout of the panel
        this.frame = frame;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout( new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.weightx=0.0;
        constraints.weighty=0.0;
        constraints.anchor=GridBagConstraints.LINE_START;
        add(itemName,constraints);
        constraints.gridy=1;
        itemCategory.setFont(itemCategory.getFont().deriveFont(Font.ITALIC));
        add(itemCategory,constraints);
        constraints.gridy=2;
        add(soldBy,constraints);
        constraints.gridy=3;
        add(reservePrice,constraints);
        constraints.gridy=4;
        add(itemClosesAt,constraints);
        constraints.gridy=5;
        add(descriptionLabel,constraints);
        constraints.gridy=6;
        constraints.weightx=1;
        constraints.weighty=1;
        constraints.gridwidth=2;
        constraints.fill=GridBagConstraints.BOTH;
        itemDescription.setBackground(getBackground());
        add(itemDescription,constraints);
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=0;
        constraints.weightx=0;
        constraints.weighty=0;
        constraints.anchor=GridBagConstraints.LINE_START;
        add(itemID, constraints);
        constraints.gridx=2;
        itemClosed.setFont(itemClosed.getFont().deriveFont(Font.BOLD));
        add(itemClosed, constraints);
        constraints.gridy=1;
        add(soldFor, constraints);
        constraints.gridy=2;
        add(wonBy,constraints);
        constraints.gridy=4;
        add(bidLabel,constraints);
        constraints.gridy=5;
        constraints.gridheight=2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty=1.0;
        constraints.weightx=1.0;
        add(new JScrollPane(bidsJList),constraints);
        constraints.gridy=7;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridheight=1;
        constraints.weightx=0.0;
        constraints.weighty=0.0;
        add(bidButton,constraints);
        //add mouse click listeners into the names of the winner and the seller
        //so it will switch to that user's user panel
        wonBy.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (item.isSuccessful()) {
                    frame.switchToUserPanel(frame.getUserFromID(item.getWinningUser()));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        soldBy.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.switchToUserPanel(frame.getUserFromID(item.getSellersUserID()));
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


    }

    public void changeItem(Item item, ArrayList<Bid> bids){
        this.item = item;
        //go through the list of bids, selecting the ones one
        BidListModel bidDLM =  new BidListModel();
        for(Bid b:bids){
            if(b.getItemID()==item.getItemID()){
                bidDLM.addElement(b);
            }
        }
        //sort the list
        bidDLM.sort();
        this.bids = bidDLM;
        //set it as the model of the JList
        bidsJList.setModel(this.bids);
        //set the labels, to the properties of the items
        itemID.setText("ID: "+item.getItemID());
        itemName.setText(item.getItemTitle());
        itemCategory.setText(item.getCategory());
        soldBy.setText("Sold By: "+frame.getUserFromID(item.getSellersUserID()).getName());
        itemDescription.setText(item.getDescription());
        itemClosesAt.setText("Closes at: "+item.getTimeClosed().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        itemClosed.setText(item.getClosed() ? "Closed" : "Open");
        //if the auction has been closed
        if (item.getClosed()){
            wonBy.setVisible(true);
            if (item.isSuccessful()){
                soldFor.setVisible(true);
                //state who the item has been won by and how much it was sold by
                wonBy.setText("Won By: "+frame.getUserFromID(item.getWinningUser()).getName());
                soldFor.setText("Sold for: "+Bid.getFormattedBid(item.getSoldFor()));
            }
            else{
                //say that the item was not sold if the auction was not successful
                soldFor.setVisible(false);
                wonBy.setText("Item not Sold");
            }
            //disable the bid button if the item has been sold
            bidButton.setEnabled(false);
        }
        else{
            //if the auction has not been closed
            wonBy.setVisible(false);
            soldFor.setVisible(false);
            bidButton.setEnabled(true);
        }
        reservePrice.setText("RP: "+Bid.getFormattedBid(item.getReservePrice()));
    }

    public void refresh(ArrayList<Bid> bidList, ArrayList<Item> items) {
        //to be called when the frame of panel is refreshed,
        Item item1  = null;
        //find the item in the list
        for (Item item2:items){
            if(item2.getItemID()==item.getItemID()){
                item1 = item2;
            }
        }
        //recall the initialisation method with the updated Item
        changeItem(item1,bidList);
    }
}
