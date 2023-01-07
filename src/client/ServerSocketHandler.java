package client;

import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler implements Runnable {

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
        Socket clientSocket = serverSocket.accept();
        serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
        serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        while (true) {
            try {
                logger.log("Waiting for connection");
                logger.log(Level.SUCCESS, "Client connected");
                String message = (String) serverInputStream.readObject();
                if (message.equalsIgnoreCase("quit")) {
                    clientSocket.close();
                    serverOutputStream.close();
                    serverInputStream.close();
                    break;
                }
                logger.log("Server socket received message from client: " + message);
                appInstance.addTextToServerMessageTextArea(message);
                logger.log("Sending reponse");
                serverOutputStream.writeObject("Sending response");
                logger.log(Level.SUCCESS, "Response sent");


            } catch (Exception e) {
                logger.log("Client disconnected");
                e.printStackTrace();
                break;
            }
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
