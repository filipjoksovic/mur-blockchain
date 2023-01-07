package test;

import console.Level;
import console.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {


    private static final Logger logger = new Logger(TestClient.class.getName());


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        socket = new Socket(host.getHostName(), 9876);

        for (int i = 0; i < 10000; i++) {
            //establish socket connection to server
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            oos.writeObject("" + i);
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            //close resources
            Thread.sleep(1000);
        }
        ois.close();
        oos.close();


    }
}
