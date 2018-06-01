import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class BidListModel extends AbstractListModel<Bid> {
    //surrounds an array list with a class to use it as ListModel
    private ArrayList<Bid> bids;

    public int getSize() {
        return bids.size();
    }



    public Bid getElementAt(int index) {
        return bids.get(index);
    }

    //sorts the array list
    public void sort(){
        Collections.sort(bids);
    }

    //add an bid to the list
    public void addElement(Bid b){
        bids.add(b);
    }

    //Initialisation of the class
    public BidListModel(){
        bids = new ArrayList<>();
    }

    public ArrayList<Bid> getAllBids(){
        return bids;
    }
}
