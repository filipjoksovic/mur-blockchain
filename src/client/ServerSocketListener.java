package client;

import console.Level;
import console.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class ServerSocketListener extends Thread {

    protected Socket socket;
    protected Main appInstance;
    protected String connection_id;
    protected int serverPort;
    ObjectInputStream serverInputStream = null;
    ObjectOutputStream serverOutputStream = null;
    List<Integer> availablePorts;

    public ServerSocketListener(Socket socket, Main appInstance, List<Integer> availablePorts) {
        this.socket = socket;
        this.appInstance = appInstance;
        this.availablePorts = availablePorts;
    }

    private static final Logger logger = new Logger(ServerSocketListener.class.getName());

    public void run() {

        try {
            serverInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                logger.log("Waiting for connection");
                logger.log(Level.SUCCESS, "Client connected");
                String message = (String) serverInputStream.readObject(); //read request from client (either could be a message, or a UUID#PORT format)
                if (connection_id == null) { //client has not yet identified itself, handle it
                    try {
                        establishClientConnection(message);
                    } catch (Exception e) {
                        logger.log(Level.CRITICAL, "Error occured when identifying client");
                        e.printStackTrace();
                        break;
                    }
                }
                if (message.equalsIgnoreCase("quit")) {
                    try {
                        closeConnection();
                    } catch (Exception e) {
                        logger.log(Level.CRITICAL, "Error closing streams");
                        e.printStackTrace();
                        break;
                    }
                }
                logger.log("Server socket received message from client: " + message);
                appInstance.addTextToServerMessageTextArea(message);


                serverOutputStream.writeObject(getPortsAsString());
                logger.log(Level.SUCCESS, "Response sent");


            } catch (Exception e) {
                logger.log("Client disconnected");
                e.printStackTrace();
                break;
            }
        }
    }

    public void notifyClient(String message) {
        logger.log("Sending message to " + connection_id);
        try {

            serverOutputStream.writeObject(message);
            logger.log(Level.SUCCESS, "Message sent successfully");
        } catch (IOException e) {
            logger.log(Level.CRITICAL, "Message not sent");
            throw new RuntimeException(e);
        }
    }

    public String getConnectionID() {
        return this.connection_id;
    }

    public int getServerPort() {
        return this.serverPort;
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

    public void establishClientConnection(String message) throws IOException {
        String[] connection_data = message.split("#");
        if (connection_data.length != 2) { //if data is not in the before mentioned format, close all streams
            logger.log(Level.CRITICAL, "Data is not in expected format");
            socket.close();
            serverOutputStream.close();
            serverInputStream.close();
        }
        connection_id = connection_data[0];
        serverPort = Integer.parseInt(connection_data[1]);
        synchronized (this) {
            availablePorts.add(serverPort);
            StringBuilder availablePortsLabelText = new StringBuilder();

            logger.log(Level.DEBUG, "New client connected: " + serverPort);
            int counter = 0;
            for (Integer possiblePort : availablePorts) {
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
    }

    public void closeConnection() throws IOException {
        socket.close();
        serverOutputStream.close();
        serverInputStream.close();
    }
}
