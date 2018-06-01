import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class User implements Serializable{

    private String givenName;
    private String familyName;
    private String emailAddress;
    private int userID;
    private int passwordHash;


    public User(String givenName,String familyName, String emailAddress){
        //create the user details
        this.givenName = givenName;
        this.familyName = familyName;
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password){
        //append the user's email to the password and then hash it
        String passwordID = password+emailAddress;
        passwordHash = passwordID.hashCode();
    }

    private void resetID(){
        //randomly generate a new user ID
        Random r = new Random();
        userID = r.nextInt();
    }

    public boolean checkAuth(Auth a){
        //check to see if a user validates an auth
        //that the password hash and the email are equal to the ones stored in the user
        return ((passwordHash==a.getPasswordHash()&&emailAddress.equals(a.getEmail())));
    }

    //generate a unique ID number for the user
    public void generateUserID(ArrayList<User> users){
        boolean uniqueID =false;
        while (!uniqueID) {
            //generate a new ID number
            resetID();
            uniqueID = true;
            for (int i = 0;i<users.size();i++) {
                //check to see if the ID number is not possessed by another user
                if (users.get(i).getUserID() == getUserID()){
                    uniqueID=false;
                }
            }
        }
    }

    //getters for various properties
    public String getEmailAddress(){
        return emailAddress;
    }

    public int getUserID() {
        return userID;
    }

    public String getName(){
        return givenName+" "+familyName;
    }
}
