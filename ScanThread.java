import java.time.LocalDateTime;
import java.util.ArrayList;

public class ScanThread implements Runnable {
//Server side thread for scanning the items to see if the auction on any of them has ended and closing the auction if it has
 private Server s;


    public void run(){
        while (true) {
            //get the items
            for (Item i : s.getItems()) {
                //if the item's closing time is before "now"
                if (i.getTimeClosed().isBefore(LocalDateTime.now())) {

                    //get the bids
                    ArrayList<Bid> bidsOnItem = new ArrayList<>();
                    for (Bid b : s.getBids()) {
                        //if the bids references the items id
                        if (b.getItemID() == i.getItemID()) {
                            //add it to a list of bids on the item
                            bidsOnItem.add(b);
                        }
                    }
                    synchronized (i) {
                        //pass the bids on the item into the items close auction method, the item class will then handel it
                        i.closeAuction(bidsOnItem);
                    }
                }
            }
            try {
                //sleep the thread for a minute
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    public ScanThread(Server s){
        this.s = s;
    }
}