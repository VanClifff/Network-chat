package server;

import exeption.AuthDoubleName;
import exeption.AuthFailException;
import exeption.AuthWrongPassword;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {

    private static final String CMD_END = "end";
    private Socket socket;
    private Server server;

    private DataOutputStream out;
    private DataInputStream in;

    private static int clientsCount = 0;
    private String clientName;

    private static final String AUTH_WRONG_PASS = "WRONG_PASS";
    private static final String AUTH_OK = "OK";
    private static final String AUTH_ALREADY_IN_USE = "ALREADY_IN_USE";

    ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            clientsCount++;
            clientName = "client" + clientsCount;

            System.out.println("Client \"" + clientName + "\" ready!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!socket.isInputShutdown()) {
            waitForAuth();
            waitForMessage();
        }
    }

    private void waitForMessage() {
        while (true) {
            String message = null;
            try {
                message = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isQuitCmd(message)) {
                killClient();
            } else {
                System.out.println(clientName + ": " + message);
            }

            new Thread(new MessagesSender(message, clientName, server)).start();
        }
    }

    private void killClient() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.removeClient(this);
        Thread.currentThread().interrupt();
    }

    private void waitForAuth() {
        while (true) {
            String message = null;
            try {
                message = in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isAuthOk(message)) {
                System.out.println(clientName + " auth ok and is ready for chat!");
                try {
                    out.writeUTF(AUTH_OK);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            } else if (isQuitCmd(message)) {
                killClient();
            }
        }
    }

    private boolean isQuitCmd(String message) {
        return CMD_END.equalsIgnoreCase(message);
    }

    private boolean isAuthOk(String message) {
        if (message != null) {
            String[] parsedMessage = message.split("___");
            if (parsedMessage.length == 3) {
                try {
                    processAuthMessage(parsedMessage);
                } catch (AuthDoubleName ee) {
                    return getErrorMessage(ee, AUTH_ALREADY_IN_USE);
                } catch (AuthFailException e) {
                    return getErrorMessage(e, AUTH_WRONG_PASS);
                }
                return true;
            }
        }
        return false;
    }

    private boolean getErrorMessage(AuthFailException e, String errorMessage) {
        System.out.println(e.getFailInfo());
        try {
            out.writeUTF(errorMessage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    private void processAuthMessage(String[] parsedMessage) throws AuthFailException {
        if (parsedMessage[0].equals("auth")) {
            System.out.println("Auth message from " + clientName);
            String login = parsedMessage[1];
            String password = parsedMessage[2];

            String nick = null;
            try {
                nick = SQLHandler.getNick(login, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (nick != null) {
                try {
                    server.addClient(this, nick);
                } catch (AuthDoubleName e) {
                    throw new AuthDoubleName(nick);
                }
                return;
            }

            throw new AuthWrongPassword(login, password);
        }
    }


    DataOutputStream getOut() {
        return out;
    }

    String getClientName() {
        return clientName;
    }

    void setNick(String nick) {
        this.clientName = nick;
    }
}
