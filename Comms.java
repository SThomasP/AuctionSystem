import java.net.*;
import java.io.*;

public class Comms {

    private String serverName;
    private int portNumber;
    private Socket socket;
    private ServerSocket serverSocket;

    public Message getMessage() throws IOException {
        //get a message from the server/client
        Message output = null;
        try {
            //get the sockets input stream
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            //read the message
            output = (Message) in.readObject();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return the message
        return output;
    }

    public void sendMessage(Message message){
        try{
            //send a message to the server
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //set the server up as if its a client
    public boolean setUpForClient(){
        try {
            //create a new Socket
            socket = new Socket(serverName,portNumber);
            return true;
        }
        //if the socket cannot connect, the error is thrown and the method returns false
        catch (IOException e) {
            return false;
        }

    }
    //gets the remote IP to the socket
    public String getRemoteIP(){
        return socket.getInetAddress().toString();
    }


    public void setUpForServer(Server server){
        //set up the comms layer for the server side
        try {
            //create a server Socket
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //create a thread to listen for clients and connect them to the server
        CommsMultiThread multiThread = new CommsMultiThread(serverSocket,server);
        //start it
        new Thread(multiThread).start();

    }

    public Comms(int port, String server){
        this.portNumber = port;
        this.serverName = server;
    }

    protected Comms(Socket socket){
        this.socket = socket;
    }
}
