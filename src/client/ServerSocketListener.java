package client;

import console.Level;
import console.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class ServerSocketListener extends Thread {

    protected Socket socket;
    protected Main appInstance;
    protected String connection_id;

    public ServerSocketListener(Socket socket, Main appInstance) {
        this.socket = socket;
        this.appInstance = appInstance;
    }

    private static final Logger logger = new Logger(ServerSocketListener.class.getName());

    public void run() {
        ObjectInputStream serverInputStream = null;
        ObjectOutputStream serverOutputStream = null;
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
                String message = (String) serverInputStream.readObject();
                if (connection_id == null) {
                    connection_id = message;
                }
                if (message.equalsIgnoreCase("quit")) {
                    socket.close();
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

    public String getConnectionID() {
        return this.connection_id;
    }
}
