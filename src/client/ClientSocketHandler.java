package client;

import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.Socket;

public class ClientSocketHandler implements Runnable {

    private final int serverPort;
    public Socket clientSocket;
    ObjectOutputStream socketOutputStream;
    ObjectInputStream socketInputStream;
    Thread clientThread;
    Main appInstance;
    private static final Logger logger = new Logger(ClientSocketHandler.class.getName());

    public ClientSocketHandler(int serverPort, Main appInstance) {
        this.serverPort = serverPort;
        this.appInstance = appInstance;

    }

    public void start() {
        logger.log("Attempting to start client socket");
        if (clientSocket != null) {
            logger.log(Level.ERROR, "Client socket is already initialized");
        } else {
            try {
                clientSocket = new Socket("localhost", serverPort);
                logger.log("Socket opened");

            } catch (IOException e) {
                logger.log("Error initializing client socket");
                throw new RuntimeException(e);
            }
            try {
                socketOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                logger.log("Streams initialized");

            } catch (Exception e) {
                logger.log("Error initializing input and output streams");
            }

            String toSend = "Hello server!!!";
            try {
                sendMessageToServer(toSend);
                logger.log(Level.SUCCESS, "Message send");
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {

    }

    public void sendMessageToServer(String message) throws IOException, ClassNotFoundException {
        try {
            logger.log("Attempting to write to server");

            socketOutputStream.writeObject(message);
            logger.log("Write finished");

        } catch (IOException e) {
            logger.log(Level.CRITICAL, "Error sending output streams");

            throw new RuntimeException(e);
        }

        try {
            if (socketInputStream == null) {
                socketInputStream = new ObjectInputStream(clientSocket.getInputStream());
            }
            String response = (String) socketInputStream.readObject();
            logger.log("Attempting to read response");
            logger.log(Level.SUCCESS, "Response received: " + response);

        } catch (Exception e) {
            logger.log(Level.CRITICAL, "Error reading response");
            e.printStackTrace();
        }

        try {
            logger.log("Attempting to close streams");

//            socketInputStream.close();
//            socketOutputStream.close();
//            clientSocket.close();
//            clientSocket = null;
        } catch (Exception e) {
            logger.log(Level.CRITICAL, "Error closing streams");
        }
    }
}
