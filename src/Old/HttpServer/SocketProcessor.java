package Old.HttpServer;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketProcessor implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public SocketProcessor(Socket s) throws Throwable {
        this.socket = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();
    }

    public void run() {
        try {
            readInputHeaders();
            writeResponse("<h1>Hello world</h1>");
        } catch (Throwable t) {
            /*do nothing*/
        } finally {
            try {
                socket.close();
            } catch (Throwable t) {
                /*do nothing*/
            }
        }
        System.err.println("Client processing finished");
    }

    public void writeResponse(String s) throws Throwable {
        String tags = "<html><body></body></html>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "server.Server: YarServer/2009-09-09\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + (s.length() + tags.length()) + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + "<html><body>" + s + "</body></html>";
        os.write(result.getBytes());
        os.flush();
    }

    public void readInputHeaders() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while (true) {
            String s = br.readLine();
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }
    }
}