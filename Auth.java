import java.io.Serializable;

//simple class for logging in a user, passes storing the information the server needs to identify them
public class Auth implements Serializable {
    private  String email;
    private int passwordHash;

    public String getEmail() {
        return email;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public Auth(String email, String password){
        this.email = email;
        passwordHash = (password+email).hashCode();
    }
}
