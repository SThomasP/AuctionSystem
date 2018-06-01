import java.io.IOException;
import java.net.ServerSocket;

public class CommsMultiThread implements Runnable {
    private ServerSocket serverSocket;
    private Server server;
    @Override
    //Thread for the server to run, It's whole purpose is to set up an new Thread every time a client connects to the server
    public void run() {
        try{
            while (true){
                //each a new client connects to the server, a new thread is created to deal with the client
                new Thread(new CommsThread(serverSocket.accept(),server)).start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public CommsMultiThread(ServerSocket ss, Server s){
        serverSocket=ss;
        server=s;
    }
}
