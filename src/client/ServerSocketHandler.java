package client;

import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ServerSocketHandler implements Runnable {

    List<Thread> connections;
    Thread serverThread;
    ServerSocket serverSocket;
    ObjectInputStream serverInputStream;
    ObjectOutputStream serverOutputStream;
    int port;

    Main appInstance;

    private static Logger logger = new Logger(ServerSocketHandler.class.getName());

    public ServerSocketHandler(int port, Main appInstance) {
        this.port = port;
        this.appInstance = appInstance;
        connections = new Vector<>();
    }

    public void serverSocketListen() throws IOException {
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

        logger.log("Listening on port: " + port);
        appInstance.setTitle("Blockchain client:: " + port);
        appInstance.setServerSocketLabel();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread serverSocketListener = new ServerSocketListener(clientSocket, appInstance);
            connections.add(serverSocketListener);
            serverSocketListener.start();

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
}
