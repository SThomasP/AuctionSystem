import java.io.IOException;
import java.net.Socket;


public class CommsThread extends Comms implements Runnable {

    //Runnable extension of Comms, for a client to connect to
    private Server server;

    public CommsThread(Socket socket,Server server){
        //set the socket and the server to the client
        super(socket);
        this.server = server;
    }

    @Override
    public void run() {
        //set things up
        Message message;
        String remoteIP = getRemoteIP();
        //add the client to the list of clients, shown on the server's GUI
        server.addClient(remoteIP);
        try {
            //try to get a new message
            while ((message = getMessage()) != null) {
                synchronized (server) {
                    //pass the message to the server and get a reply from it
                    Message reply = server.dealWithMessage(message, getRemoteIP());
                    //if the reply is not a null message, send it
                    if (reply!=null) {
                        sendMessage(reply);
                    }
                }
            }
        }
        //Once a client disconnects from the server, remove it from the list
        catch (IOException e){
            server.removeClient(remoteIP);
        }
        }
    }
