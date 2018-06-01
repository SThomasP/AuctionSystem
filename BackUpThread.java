public class BackUpThread implements Runnable {

    private Server s;

    public void run(){
        while (true){
            synchronized (s){
                //runs the server's back up method, keeping data persistence
                s.backUpData();
            }
            try {
                //runs every minutes
                Thread.sleep(60000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public BackUpThread(Server s){
        this.s = s;
    }

}
