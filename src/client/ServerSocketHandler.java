package client;

import blockchain.BlockUtils;
import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ServerSocketHandler implements Runnable {

    List<Integer> availablePorts;
    Thread serverThread;
    ServerSocket serverSocket;
    ObjectInputStream serverInputStream;
    ObjectOutputStream serverOutputStream;
    List<Integer> knownPorts;
    int port;

    Main appInstance;

    BlockUtils blockUtils = new BlockUtils();


    private static Logger logger = new Logger(ServerSocketHandler.class.getName());

    public ServerSocketHandler(int port, Main appInstance) {
        this.port = port;
        this.appInstance = appInstance;
        availablePorts = new Vector<>();

    }

    public void serverSocketListen() throws IOException {
        synchronized (this) {
            while (true) {
                try {
                    serverSocket = new ServerSocket(port);
                    logger.log("Server socket initialized on port: " + port);
                    break;
                } catch (IOException e) {
                    logger.log(Level.CRITICAL, "Port " + port + " is already in " + "use. Retrying with different port");
                    ++port;
                }
            }
        }

        logger.log("Listening on port: " + port);
        appInstance.setTitle("Blockchain client:: " + port);
        appInstance.setServerSocketLabel();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            logger.log("Starting listener thread");
            ServerSocketListener serverSocketListener = new ServerSocketListener(clientSocket, appInstance, availablePorts);
            logger.log(Level.SUCCESS, "Listener thread connected");
            serverSocketListener.start();
//            System.out.println("Number of connnections: " + connections.size());
        }
    }

    public void start() {
        if (serverThread == null) {
            logger.log("Starting server thread");
            serverThread = new Thread(this, "Server thread");
            serverThread.start();
            logger.log(Level.SUCCESS, "Server thread started");
        } else {
            logger.log(Level.ERROR, "Server thread already started");
        }
    }

    @Override
    public void run() {
        try {
            this.serverSocketListen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPortsAsString() {
        synchronized (this) {

            StringBuilder s = new StringBuilder();
            for (int i = 0; i < availablePorts.size(); i++) {
                s.append(availablePorts.get(i));
                if (i < availablePorts.size() - 1) {
                    s.append("@");
                }
            }

            return s.toString();
        }
    }


}
