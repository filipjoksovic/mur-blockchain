package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocketHandler implements Runnable {

    private final int serverPort;
    public Socket clientSocket;
    DataOutputStream socketOutputStream;
    Thread clientThread;

    public ClientSocketHandler(int serverPort) {
        this.serverPort = serverPort;

    }

    public void start() {
        if (clientThread != null) {
            System.out.println("[ERROR]: Client thread already started");
        } else {
            System.out.println("[INFO]: Initializing client socket");
            clientThread = new Thread(this, "Client thread");
            clientThread.start();
            System.out.println("[INFO]: Client socket initialized");
        }
    }

    @Override
    public void run() {
        if (clientSocket == null) {
            try {
                System.out.println("[INFO]: Opening socket to port: " + serverPort);
                clientSocket = new Socket("localhost", serverPort);
                System.out.println("[INFO]: Socket opened");
            } catch (IOException e) {
                System.out.println("[CRITICAL]: Error starting socket on port: " + serverPort);
                throw new RuntimeException(e);
            }
        }
        try {
            System.out.println("[INFO]: Sending message to server");
            sendMessageToServer("Hello server!");
            System.out.println("[INFO]: Message sent");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToServer(String message) throws IOException {
        socketOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        socketOutputStream.writeUTF(message);
        socketOutputStream.flush();
        socketOutputStream.close();
    }
}
