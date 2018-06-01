import java.io.Serializable;
import java.text.DecimalFormat;

public  class Bid implements Comparable<Bid> , Serializable{
    private int userID;
    private double price;
    private int itemID;

    @Override

    //simple data storage class for bids
    public int compareTo(Bid o) {
        return (int) (this.price - o.price);
    }

    public Bid (int userID, int itemID, double price){
        this.price=price;
        this.itemID=itemID;
        this.userID=userID;
    }

    public double getPrice(){
        return price;
    }

    public int getUserID(){
        return userID;
    }

    public int getItemID() {
        return itemID;
    }

    //static class for formatting a bid amount
    public static String getFormattedBid(double amount){
        DecimalFormat d = new DecimalFormat("£#0.00");
        return  d.format(amount);
    }

    //non static class for formatting the bid itself, to look like a currency amount
    public String getFormattedBid(){
        DecimalFormat d = new DecimalFormat("£#0.00");
        return d.format(price);
    }

    public String toString(){
        return getFormattedBid();
    }
}