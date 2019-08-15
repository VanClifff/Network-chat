package Old.HttpServer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yar 09.09.2009
 */
public class HttpServer {

    public static void main(String[] args) throws Throwable {
        ServerSocket ss = new ServerSocket(8083);
        while (true) {
            Socket s = ss.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
        }
    }

}

