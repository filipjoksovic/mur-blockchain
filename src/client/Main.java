package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import blockchain.BlockMinerThread;
import blockchain.BlockUtils;
import console.Level;
import console.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;


public class Main {
    int posX = 0;
    int posY = 0;
    int width = 500;
    int height = 950;
    int padding = 10;
    int componentWidth = width - 4 * padding;
    int componentHeight = 30;

    ClientSocketHandler csh;
    ServerSocketHandler sch;

    BlockUtils blockUtils;

    JFrame frame;
    JButton serverSocketButton;
    JButton clientSocketButton;
    JButton mineBlockchainButton;
    JLabel serverSocketStatus;
    JLabel clientSocketStatus;
    JLabel numConnections;
    JLabel portsAvailable;

    JTextField clientMessageInput;
    JButton clientMessageSendButton;

    JTextArea serverMessagesTextArea;
    JTextArea serverResponsesTextArea;

    JTextArea blockChainData;
    JScrollPane sbrBlockchain;

    JScrollPane sbrServerMessages;

    private static Logger logger = new Logger(Main.class.getName());

    Main(int posX, int posY) {
        blockUtils = new BlockUtils();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }
        this.posX = posX;
        this.posY = posY;
        frame = new JFrame();
        frame.setLocation(this.posX, this.posY);
        frame.setSize(width, height);
        frame.setTitle("Blockchain client");
        frame.setLayout(null);
        frame.setVisible(true);


        initComponents();
        initActionListeners();
        initBounds();
        addComponents();

        serverSocketButton.doClick();
//        clientSocketButton.doClick();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.repaint();

    }

    public static void main(String[] args) throws IOException {

        Main main = new Main(10, 200);
//        Main main2 = new Main(main.posX + main.width + main.padding, 200);
//        Main main3 = new Main(main2.posX + main2.width + main2.padding, 200);
//        Main main4 = new Main(main3.posX + main2.width + main2.padding, 200);
//        Main main5 = new Main(main4.posX + main2.width + main2.padding, 200);
//        Main main6 = new Main(main5.posX + main2.width + main2.padding, 200);

        BasicConfigurator.configure();

    }

    public void setServerSocketLabel() {
        if (serverSocketStatus == null) {
            serverSocketStatus = new JLabel("Server socket status: Off");
        } else {
            serverSocketStatus.setText("Server socket status: On ::" + sch.port);
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

    public void setPossiblePortsLabel(String text) {
        portsAvailable.setText(text);
    }

    void initActionListeners() {
        Main appInstance = this;

        serverSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sch == null) {
                    sch = new ServerSocketHandler(9090, appInstance, blockUtils);
                    sch.start();
                    logger.log(Level.CRITICAL, String.valueOf(sch.port));
                } else {
                    logger.log("Server socket is already " + "initialized");

                }
            }
        });

        clientSocketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (csh == null) {
                    if (sch == null) {
                        logger.log(Level.ERROR, "Client socket cannot initialize before server socket is initialized.");
                        return;
                    }

                    csh = new ClientSocketHandler(9090, appInstance, sch.port, blockUtils);
                    csh.start();

                    logger.log(Level.DEBUG, "Server instance port: " + sch.port);
                } else {
                    logger.log("Client socket is already initialized");
                }
            }
        });

        clientMessageSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (csh == null) {
                    logger.log(Level.ERROR, "Client socket not started yet. Start client socket and then try again.");
                } else {
                    logger.log("Sending message to server");
                    for (int i = 0; i < 1000; i++) {
                        csh.sendMessageToServers(clientMessageInput.getText());
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    logger.log("Message sent to server");

                }
            }
        });

        mineBlockchainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                BlockMinerThread bmt = new BlockMinerThread();
//                bmt.start();
                csh.startMiningBlockchain();
            }

        });
    }

    public void initBounds() {
        serverSocketStatus.setBounds(padding, padding, componentWidth, componentHeight);
        clientSocketStatus.setBounds(padding, serverSocketStatus.getY() + serverSocketStatus.getHeight() + padding, componentWidth, componentHeight);
        numConnections.setBounds(padding, clientSocketStatus.getY() + clientSocketStatus.getHeight() + padding, componentWidth, componentHeight);
        serverSocketButton.setBounds(padding, numConnections.getY() + numConnections.getHeight() + padding, componentWidth / 2 - padding, componentHeight);
        clientSocketButton.setBounds(serverSocketButton.getX() + serverSocketButton.getWidth() + padding, numConnections.getY() + numConnections.getHeight() + padding, componentWidth / 2 - padding, componentHeight);
        clientMessageInput.setBounds(padding, clientSocketButton.getY() + clientSocketButton.getHeight() + padding, (int) (componentWidth * 0.7) - padding, componentHeight);
        clientMessageSendButton.setBounds(clientMessageInput.getX() + clientMessageInput.getWidth() + padding, clientSocketButton.getY() + clientSocketButton.getHeight() + padding, (int) (componentWidth * 0.3), componentHeight);
        sbrServerMessages.setBounds(padding, clientMessageSendButton.getY() + clientMessageInput.getHeight() + padding, componentWidth, componentHeight * 10);
        mineBlockchainButton.setBounds(padding, sbrServerMessages.getY() + sbrServerMessages.getHeight() + padding, componentWidth, componentHeight);

        sbrBlockchain.setBounds(padding, mineBlockchainButton.getY() + mineBlockchainButton.getHeight() + padding, componentWidth, componentHeight * 10);

        portsAvailable.setBounds(padding, serverResponsesTextArea.getY() + serverResponsesTextArea.getHeight() + padding, componentWidth, componentHeight * 10);
    }

    public void addComponents() {
        frame.add(serverSocketStatus);
        frame.add(clientSocketStatus);
        frame.add(numConnections);
        frame.add(serverSocketButton);
        frame.add(clientSocketButton);
        frame.add(clientMessageInput);
        frame.add(clientMessageSendButton);
//        frame.add(serverMessagesTextArea);
        frame.add(sbrServerMessages);
        frame.add(portsAvailable);
        frame.add(mineBlockchainButton);
//        frame.add(serverResponsesTextArea);
        frame.add(sbrBlockchain);
    }

    public void initComponents() {
        serverSocketButton = new JButton("Start server");
        clientSocketButton = new JButton("Start client socket");
        mineBlockchainButton = new JButton("Start mining blockchain");
        serverSocketStatus = new JLabel("Server socket status: Off");
        clientSocketStatus = new JLabel("Client socket status: OFF");
        numConnections = new JLabel("To see the number of connections turn on server mode first");
        portsAvailable = new JLabel("");

        clientMessageInput = new JTextField();
        clientMessageSendButton = new JButton("Send message");

        serverMessagesTextArea = new JTextArea();
        sbrServerMessages = new JScrollPane(serverMessagesTextArea);
        sbrServerMessages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        serverResponsesTextArea = new JTextArea();
        blockChainData = new JTextArea();

        sbrBlockchain = new JScrollPane(blockChainData);
        sbrBlockchain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }


}
