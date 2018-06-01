import java.io.Serializable;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Item implements Serializable {

    //static array list of categories
    public static final String[] categories = {"Abstract Concepts", "Alcohol", "Coffee", "Contraband", "Electronics", "Entertainment", "Food", "Furniture"};

    private String category;
    private String itemTitle;
    private int itemID;
    private int sellersUserID;
    private String description;
    private LocalDateTime timeStarted;
    private LocalDateTime timeClosed;
    private boolean closed;
    private double reservePrice;
    private int winningUser;
    private double soldFor;
    private boolean successful;

    //getters for the various properties of Items
    public boolean isSuccessful() {
        return successful && closed;
    }

    public double getSoldFor() {
        return soldFor;
    }

    public int getWinningUser() {
        return winningUser;
    }

    public String getCategory() {
        return category;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public int getItemID() {
        return itemID;
    }

    public int getSellersUserID() {
        return sellersUserID;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public LocalDateTime getTimeClosed() {
        return timeClosed;
    }

    public boolean getClosed() {
        return closed;
    }

    public double getReservePrice() {
        return reservePrice;
    }


    //the list of bids are passed in the auction is closed
    public void closeAuction(ArrayList<Bid> bids) {
        //set the auction to closed
        closed = true;
        //if there are no bids
        if (!bids.isEmpty()){
            //find the top bid
            Bid topBid = Collections.max(bids);
            //get the winning users properties
            winningUser = topBid.getUserID();
            soldFor = topBid.getPrice();
            successful =true;
        }
        else {
            //set
            soldFor = 0;
            successful =false;
        }
    }

    public Item(String itemTitle, String description, String category, int userID, double reservePrice, LocalDateTime timeClosed) {
        //initialise the item, with all the properties
        timeStarted = LocalDateTime.now();
        this.timeClosed = timeClosed;
        this.itemTitle = itemTitle;
        this.description = description;
        this.category = category;
        this.sellersUserID = userID;
        this.reservePrice = reservePrice;
        closed = false;
    }

    private void setItemID() {
        //generates a random item ID
        Random r = new Random();
        itemID = r.nextInt();
    }

    public void generateItemID(ArrayList<Item> items) {
        //generates a unique ID number for the item
        boolean uniqueID = false;
        while (!uniqueID) {
            setItemID();
            uniqueID = true;
            for (int i = 0; i < items.size(); i++) {
                //
                if (items.get(i).getItemID() == getItemID()) {
                    uniqueID = false;
                }
            }
        }
    }

    public String toString() {
        //cheating a bid, but allows this to appear nicely in list renders
        return itemTitle;
    }
}
