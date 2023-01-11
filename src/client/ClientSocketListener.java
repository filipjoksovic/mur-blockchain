package client;

import blockchain.Block;
import blockchain.BlockUtils;
import console.Level;
import console.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class ClientSocketListener extends Thread {

    UUID connectionID;
    Socket socket;
    int port;
    int parentPort;
    Main appInstance;
    List<Integer> availablePorts;
    ObjectInputStream socketInputStream;
    ObjectOutputStream socketOutputStream;
    private static final Logger logger = new Logger(ClientSocketListener.class.getName());
    BlockUtils blockUtilsInstance;

    public ClientSocketListener(int port, Main appInstance, int parentPort) throws IOException {
        connectionID = UUID.randomUUID();
        this.socket = new Socket("localhost", port);
        this.port = port;
        this.parentPort = parentPort;
        this.appInstance = appInstance;

        logger.log(Level.IDIOT, "Creating new socket/connection");

        socketOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

    }

    public void run() {
        try {
            sendMessageToServer(connectionID + "#" + this.parentPort);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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
            logger.log("Waiting for message from server");
            if (socketInputStream == null) {
                socketInputStream = new ObjectInputStream(socket.getInputStream());
            }
            String response = (String) socketInputStream.readObject();
            logger.log("Message received");
            appInstance.serverResponsesTextArea.append(response + "\n");
            String[] blockData = response.split("//");
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
//        try {
//            logger.log("Attempting to close streams");
//
//        } catch (Exception e) {
//            logger.log(Level.CRITICAL, "Error closing streams");
//        }
    }

}
