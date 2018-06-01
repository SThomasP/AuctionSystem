import java.io.*;
import java.util.ArrayList;


public class DataPersistence {

    private File itemFile, userFile, bidFile, logFile;

    public void writeItemsToFile(ArrayList<Item> items){
        //write the list of items to the file
        try{
            //open a new output stream
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(itemFile));
            //write the object
            oos.writeObject(items);
            //close the stream
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeLogToFile(String toLog){
        try{
            //append the log file with the latest log string
            FileWriter fw = new FileWriter(logFile,true);
            fw.write(toLog+"\n");
            fw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeBidsToFile(ArrayList<Bid> bids){
        try{
            //rewrite the bids file
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(bidFile));
            oos.writeObject(bids);
            //close the stream
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeUsersToFile(ArrayList<User> users){
        try{
            //rewrite the users files, with the latest list of users
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile));
            oos.writeObject(users);
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<Item> loadItemsFromFile() throws Exception{
        //read the array list of items from the item file
        ArrayList<Item> toReturn;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(itemFile));
        toReturn = (ArrayList<Item>) ois.readObject();
        ois.close();
        return toReturn;
    }




    public ArrayList<User> loadUsersFromFile() throws Exception{
        //read the array list of users from the users file
        ArrayList<User> toReturn;
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile));
            toReturn = (ArrayList<User>) ois.readObject();
            ois.close();
        return toReturn;
    }

    public ArrayList<Bid> loadBidsFromFile() throws Exception{
        //read the array list of bids from the bids file and return it
        ArrayList<Bid> toReturn;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bidFile));
        toReturn = (ArrayList<Bid>) ois.readObject();
        ois.close();
        return toReturn;
    }



    public DataPersistence(String itemFile, String userFile,  String bidFile, String logFile){
        this.itemFile = new File(itemFile);
        this.userFile = new File(userFile);
        this.bidFile = new File(bidFile);
        this.logFile = new File(logFile);
    }
}
