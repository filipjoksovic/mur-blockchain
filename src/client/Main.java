package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import console.Level;
import console.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;


public class Main {

    int width = 500;
    int height = 800;
    int padding = 10;
    int componentWidth = width - 4 * padding;
    int componentHeight = 30;

    ServerSocketHandler serverSocketHandler;
    ClientSocketHandler clientSocketHandler;

    JFrame frame;
    JButton serverSocketButton;
    JButton clientSocketButton;
    JLabel serverSocketStatus;
    JLabel clientSocketStatus;
    JLabel numConnections;

    JTextField clientMessageInput;
    JButton clientMessageSendButton;

    JTextArea serverMessagesTextArea;
    private static Logger logger = new Logger(Main.class.getName());

    Main() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }

        frame = new JFrame();

        frame.setSize(width, height);
        frame.setTitle("Blockchain client");
        frame.setLayout(null);
        frame.setVisible(true);
        Main appInstance = this;


        serverSocketButton = new JButton("Start server");
        clientSocketButton = new JButton("Start client socket");
        serverSocketStatus = new JLabel("Server socket status: Off");
        clientSocketStatus = new JLabel("Client socket status: OFF");
        numConnections = new JLabel("To see the number of connections turn on server mode first");

        clientMessageInput = new JTextField();
        clientMessageSendButton = new JButton("Send message");

        serverMessagesTextArea = new JTextArea();

        serverSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverSocketHandler == null) {
                    serverSocketHandler = new ServerSocketHandler(9090, appInstance);
                    serverSocketHandler.start();
                } else {
                    logger.log("Server socket is already " + "initialized");

                }
            }
        });

        clientSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientSocketHandler == null) {
                    if (serverSocketHandler == null) {
                        logger.log(Level.ERROR, "Client socket cannot initialize before server socket is initialized.");
                        return;
                    }
                    clientSocketHandler = new ClientSocketHandler(9090, appInstance);
                    clientSocketHandler.start();
                } else {
                    logger.log("Client socket is already initialized");
                }
            }
        });

        clientMessageSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientSocketHandler == null) {
                    logger.log(Level.ERROR, "Client socket not started yet. Start client socket and then try again.");
                } else {
                    logger.log("Sending message to server");
                    try {
                        clientSocketHandler.sendMessageToServer(clientMessageInput.getText());
                        logger.log("Message sent to server");

                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });


        serverSocketStatus.setBounds(padding, padding, componentWidth, componentHeight);
        clientSocketStatus.setBounds(padding, serverSocketStatus.getY() + serverSocketStatus.getHeight() + padding, componentWidth, componentHeight);
        numConnections.setBounds(padding, clientSocketStatus.getY() + clientSocketStatus.getHeight() + padding, componentWidth, componentHeight);
        serverSocketButton.setBounds(padding, numConnections.getY() + numConnections.getHeight() + padding, componentWidth / 2 - padding, componentHeight);
        clientSocketButton.setBounds(serverSocketButton.getX() + serverSocketButton.getWidth() + padding, numConnections.getY() + numConnections.getHeight() + padding, componentWidth / 2 - padding, componentHeight);
        clientMessageInput.setBounds(padding, clientSocketButton.getY() + clientSocketButton.getHeight() + padding, (int) (componentWidth * 0.7) - padding, componentHeight);
        clientMessageSendButton.setBounds(clientMessageInput.getX() + clientMessageInput.getWidth() + padding, clientSocketButton.getY() + clientSocketButton.getHeight() + padding, (int) (componentWidth * 0.3), componentHeight);
        serverMessagesTextArea.setBounds(padding, clientMessageInput.getY() + clientMessageInput.getHeight() + padding, componentWidth, componentHeight * 10);

        frame.add(serverSocketStatus);
        frame.add(clientSocketStatus);
        frame.add(numConnections);
        frame.add(serverSocketButton);
        frame.add(clientSocketButton);
        frame.add(clientMessageInput);
        frame.add(clientMessageSendButton);
        frame.add(serverMessagesTextArea);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.repaint();

    }

    public static void main(String[] args) throws IOException {

        Main main = new Main();
        BasicConfigurator.configure();

    }

    public void setServerSocketLabel() {
        if (serverSocketStatus == null) {
            serverSocketStatus = new JLabel("Server socket status: Off");
        } else {
            serverSocketStatus.setText("Server socket status: On ::" + serverSocketHandler.port);
        }
    }

    public void setClientSocketLabel() {
        clientSocketStatus = new JLabel("Client socket status: OFF");
    }

    public void setTitle(String title) {
        this.frame.setTitle(title);
    }

    public void addTextToServerMessageTextArea(String message) {
        this.serverMessagesTextArea.append(message + "\n");
    }

    public void setNumberConnectionsLabel(int num) {
        numConnections.setText("Number of connections: " + num);
    }


}
