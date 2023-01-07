package test;

import console.Level;
import console.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer implements Runnable {
    private final static Logger logger = new Logger(TestServer.class.getName());


    private static ServerSocket server;
    private static int port = 9876;
    Socket socket = null;


    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        TestServer ts = new TestServer();
        try {
            server = new ServerSocket(port);
            logger.log("Server started at port: " + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            ts.socket = server.accept();
            logger.log("Client connected");
            ts.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("New Thread started");

    }

    public void start() {
        Thread t = new Thread(this, "Thread 1");
        t.start();
    }

    @Override
    public void run() {
        logger.log("Thread started");
        while (true) {
            System.out.println("Waiting for the client request");
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String message = null;
            try {
                message = (String) ois.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Message Received: " + message);
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                oos.writeObject("Hi Client " + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (message.equalsIgnoreCase("exit")) break;
        }
    }
}
