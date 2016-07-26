import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.*;
public class ThreadPooledServer implements Runnable{

    protected HashMap<String, String> hmap;
    protected ArrayList<User> messages;
    protected int          serverPort   ;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
        Executors.newFixedThreadPool(10);

    public ThreadPooledServer(int port){
        this.serverPort = port;
	hmap = new HashMap<String, String>();
	messages=new ArrayList<>();
    }

    public void run(){
    	
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
        	System.out.println("in while");
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("in while 1");  
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
		System.out.println("Size in Pool "+ hmap.size()); 
            this.threadPool.execute(
                new WorkerRunnable(clientSocket,
                    "Thread Pooled Server",hmap,messages));
          
        }
       this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
         InetAddress addr = InetAddress.getByName("172.31.35.129");
            this.serverSocket = new ServerSocket(this.serverPort,10,addr);
            System.out.println("Socket open");
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
    public static void main(String[] args) throws UnknownHostException, IOException {
		ThreadPooledServer server=new ThreadPooledServer(5050);
		server.run();
		
	}
}

