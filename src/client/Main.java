package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    int width = 400;
    int height = 400;
    int padding = 10;
    int componentWidth = width - 4 * padding;
    int componentHeight = 30;

    JFrame frame;
    JButton serverSocketButton;
    JButton clientSocketButton;
    ServerSocketHandler serverSocketHandler;
    ClientSocketHandler clientSocketHandler;

    JLabel serverSocketStatus;
    JLabel clientSocketStatus;
    JLabel numConnections;
    private static Logger logger = LogManager.getLogger(Main.class);

    Main() {
        frame = new JFrame();

        frame.setSize(width, height);
        frame.setTitle("Blockchain client");
        frame.setLayout(null);
        frame.setVisible(true);
        Main appInstance = this;


        serverSocketButton = new JButton("Start server");
        clientSocketButton = new JButton("Start client socket");

        setServerSocketLabel();
        setClientSocketLabel();

        serverSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverSocketHandler == null) {
                    serverSocketHandler = new ServerSocketHandler(9090, appInstance);
                    serverSocketHandler.start();
                } else {
                    System.out.println("[INFO]: Server socket is already " + "initialized");

                }
            }
        });

        clientSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clientSocketHandler == null) {
                    if (serverSocketHandler == null) {
                        System.out.println("[ERROR]: Client socket cannot initialize before server socket is initialized.");
                        return;
                    }
                    clientSocketHandler = new ClientSocketHandler(9090);
                    clientSocketHandler.start();
                } else {
                    System.out.println("[INFO]: Client socket is already initialized");
                }
            }
        });


        serverSocketStatus.setBounds(padding, padding, componentWidth, componentHeight);
        clientSocketStatus.setBounds(padding, serverSocketStatus.getY() + serverSocketStatus.getHeight() + padding, componentWidth, componentHeight);
        serverSocketButton.setBounds(padding, clientSocketStatus.getY() + clientSocketStatus.getHeight() + padding, componentWidth, componentHeight * 2);
        clientSocketButton.setBounds(padding, serverSocketButton.getY() + serverSocketButton.getHeight() + padding, componentWidth, componentHeight * 2);

        frame.add(serverSocketStatus);
        frame.add(clientSocketStatus);
        frame.add(serverSocketButton);
        frame.add(clientSocketButton);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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


}
