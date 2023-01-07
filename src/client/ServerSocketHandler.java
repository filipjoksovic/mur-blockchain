package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler implements Runnable {

    Thread serverThread;
    ServerSocket serverSocket;
    DataInputStream serverInputStream;
    int port;

    Main appInstance;

    public ServerSocketHandler(int port, Main appInstance) {
        this.port = port;
        this.appInstance = appInstance;
    }

    public void serverSocketListen() throws IOException {
        while (true) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("[INFO]: Server socket initialized on " +
                        "port: " + port);
                break;
            } catch (IOException e) {
                System.out.println("[CRITICAL]: Port " + port + " is already in " +
                        "use. Retrying with different port");
                ++port;
            }
        }

        System.out.println("[INFO]: Listening on port: " + port);
        appInstance.setTitle("Blockchain client:: " + port);
        appInstance.setServerSocketLabel();

        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                serverInputStream = new DataInputStream(clientSocket.getInputStream());
                String message = serverInputStream.readUTF();
                System.out.println();
                System.out.println("[INFO]: Server socket received message from client: " + message);
                System.out.println();
            } catch (Exception e) {
                System.out.println("[INFO]: Client disconnected");
            }
        }
    }

    public void start() {
        if (serverThread == null) {
            System.out.println("[INFO]: Starting server thread");
            serverThread = new Thread(this, "Server thread");
            serverThread.start();
            System.out.println("[SUCCESS]: Server thread started");
        } else {
            System.out.println("[ERROR]: Server thread already started");
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
