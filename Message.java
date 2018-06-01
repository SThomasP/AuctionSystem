import java.io.Serializable;

public class Message implements Serializable{

    //the different types of message, used to identify the attachment
    public static final int userRegistration = 0;
    public static final int itemRegistration = 1;
    public static final int getItems = 2;
    public static final int loginAuth = 3;
    public static final int getBids= 4;
    public static final int getUsers = 5;
    public static final int bidRegistration = 6;

    //direction of the message, for logging purposes
    public static final int toServer = 0;
    public static final int toClient = 1;


    //Object attachment on the message along with variables for direction and message type
   private Object attachment;
    private int direction;
    private int messageType;

    //getters for the direction and message type
    public int getDirection() {
        return direction;
    }


    public int getMessageType() {
        return messageType;
    }

    //create the message, with it's direction and it's message type
    public Message(int direction, int messageType){
        this.messageType = messageType;
        this.direction = direction;
        attachment =null;
    }

    //get and set the attachment
    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
}
