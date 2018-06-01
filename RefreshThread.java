public class RefreshThread implements Runnable {


    //client side thread for routine refreshment of the data from the server
    private Client c;

    public void run() {
        while (true){
            synchronized (c){
                //call the client's refresh method
                c.refresh(false);
            }
            try {
                //sleep for a minute
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public RefreshThread(Client c){
        this.c = c;
    }
}
