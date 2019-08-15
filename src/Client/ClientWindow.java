package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientWindow extends JFrame {

    private static final String AUTH_OK = "OK";
    private static final String AUTH_WRONG_PASS = "WRONG_PASS";
    private static final String AUTH_ALREADY_IN_USE = "ALREADY_IN_USE";
    private static final String WELCOME_TO_CHAT = "Welcome to chat room.";
    private static final String MSG_WRONG_PASSWORD = "Wrong username or password.";
    private static final String MSG_USERNAME_ALREADY_IN_USE = "Username is already in use. Choose another.";
    private static final String CMD_END = "end";
    private JTextField clientMsgElement;
    private JTextArea serverMsgElement;
    private JPanel authPanel;

    final String serverHost;
    final int serverPort;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public ClientWindow(String host, int port) {
        serverHost = host;
        serverPort = port;

        initConnection();
        initServerListner();
        initGUI();
    }

    private void initConnection() {
        try {
            socket = new Socket(serverHost, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGUI() {
        setBounds(600, 300, 500, 500);
        setTitle("Chat Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //for auth
        authPanel = new JPanel(new GridLayout());
        JTextField jtfLogin = new JTextField(); //"Login"
        JTextField jtfPass = new JTextField(); //"Password"
        JButton jbAuth = new JButton("Auth me");
        authPanel.add(jtfLogin);
        authPanel.add(jtfPass);
        authPanel.add(jbAuth);
        jtfLogin.setToolTipText("Enter Login");
        jtfPass.setToolTipText("Enter Password");
        add(authPanel, BorderLayout.NORTH);
        jbAuth.addActionListener(e -> {
            try {
                sendAuthCommand(jtfLogin.getText(), jtfPass.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });


        //многострочный элемент для всех сообщений
        serverMsgElement = new JTextArea();
        serverMsgElement.setEditable(false);
        serverMsgElement.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(serverMsgElement);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        //кнопка отправки сообщения
        JButton sendButton = new JButton("SEND");
        bottomPanel.add(sendButton, BorderLayout.EAST);
        clientMsgElement = new JTextField();
        bottomPanel.add(clientMsgElement, BorderLayout.CENTER);

        //отправка по кнопке
        sendButton.addActionListener(e -> {
            String message = clientMsgElement.getText();
            try {
                sendMessage(message);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            clientMsgElement.grabFocus();
        });

        //отправка по Enter
        clientMsgElement.addActionListener(e -> {
            String message = clientMsgElement.getText();
            try {
                sendMessage(message);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    out.writeUTF(CMD_END);
                    out.flush();
                    socket.close();
                    out.close();
                    in.close();
                } catch (IOException exc) {
                    System.err.println(exc.getMessage());
                    exc.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    private void sendAuthCommand(String login, String password) throws IOException {
        String command = "auth___" + login + "___" + password;
        out.writeUTF(command);
        out.flush();
    }

    private void initServerListner() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if(message.equalsIgnoreCase("end session")) break;
                    if(message.equals(AUTH_OK)) {
                        authPanel.setVisible(false);
                        serverMsgElement.append(WELCOME_TO_CHAT + "\n");
                    }
                    if(message.equals(AUTH_WRONG_PASS)) {
                        // says user about wrong password or username
                        serverMsgElement.append(MSG_WRONG_PASSWORD + "\n");
                    }
                    else if(message.equals(AUTH_ALREADY_IN_USE)) {
                        // says, that username is already in use
                        serverMsgElement.append(MSG_USERNAME_ALREADY_IN_USE + "\n");
                    }
                    else serverMsgElement.append(message + "\n");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String message) throws IOException {
        if (!message.trim().isEmpty()) {
            out.writeUTF(message);
            out.flush();
            clientMsgElement.setText("");
        }
    }
}
