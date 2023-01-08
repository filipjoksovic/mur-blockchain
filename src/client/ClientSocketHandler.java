package client;

import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientSocketHandler {

    private final int serverPort;
    public Socket clientSocket;
    ObjectOutputStream socketOutputStream;
    ObjectInputStream socketInputStream;
    Main appInstance;
    int instancePort;
    UUID connection_id;

    private static final Logger logger = new Logger(ClientSocketHandler.class.getName());

    public ClientSocketHandler(int serverPort, Main appInstance, int instancePort) {
        this.serverPort = serverPort;
        this.appInstance = appInstance;
        this.instancePort = instancePort;
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
                socketInputStream = new ObjectInputStream(clientSocket.getInputStream());

                logger.log("Streams initialized");

            } catch (Exception e) {
                logger.log("Error initializing input and output streams");
            }
            String toSend = String.valueOf(UUID.randomUUID() + "#" + this.instancePort);
            try {
                sendMessageToServer(toSend);
                logger.log(Level.SUCCESS, "Message send");
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
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
            String response = (String) socketInputStream.readObject();
            String[] possiblePortData = response.split("@");
            if (possiblePortData.length > 0) {
                int counter = 0;
                StringBuilder availablePortsLabelText = new StringBuilder();
                logger.log(Level.SUCCESS, "PORT NUMBERS RECEIVED");
                for (String possiblePort : possiblePortData) {
                    logger.log(Level.CRITICAL, "Port: " + possiblePort);
                    availablePortsLabelText.append(possiblePort);
                    if (counter % 2 == 0) {
                        availablePortsLabelText.append(" \t ");
                    } else {
                        availablePortsLabelText.append(" \n ");
                    }
                    counter++;
                }
                appInstance.setPossiblePortsLabel("Available ports: \n" + availablePortsLabelText.toString());
            }
            logger.log("Attempting to read response");
            logger.log(Level.SUCCESS, "Response received: " + response);

        } catch (Exception e) {
            logger.log(Level.CRITICAL, "Error reading response");
            e.printStackTrace();
        }

        try {
            logger.log("Attempting to close streams");

        } catch (Exception e) {
            logger.log(Level.CRITICAL, "Error closing streams");
        }
    }
}
