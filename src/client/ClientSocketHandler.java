package client;

import blockchain.BlockMinerThread;
import console.ConsoleColor;
import console.Level;
import console.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSocketHandler {

    private final int serverPort;
    private final List<ClientSocketListener> availableConnections;
    public List<ClientSocketListener> clientSockets;
    public List<Integer> knownPorts;
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
        this.clientSockets = new Vector<>();
        this.knownPorts = new Vector<>(List.of(9090, 9091, 9092, 9093, 9094, 9095, 9096, 9097, 9078, 9099));
        this.availableConnections = new Vector<>();
    }

    public void start() {
        logger.log("Attempting to start client sockets");
        for (Integer knownPort : knownPorts) {
            try {
                logger.log("Attempting to connect to " + knownPort);
                ClientSocketListener clientSocketListener = new ClientSocketListener(knownPort, appInstance, instancePort);
                logger.log("Client socket listener thread started for port: " + knownPort);
                availableConnections.add(clientSocketListener);
                clientSocketListener.start();

            } catch (IOException e) {
                logger.log(Level.WARNING, "Client socket listener failed to start for port " + knownPort);
            }
        }

    }

    public void sendMessageToServers(String message) {
        for (ClientSocketListener listener : availableConnections) {
            try {
                listener.sendMessageToServer(message);
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.CRITICAL, "Could not send message from listener thread at port : " + listener.port);
                throw new RuntimeException(e);
            }
        }
    }

    public void startMiningBlockchain() {
        BlockMinerThread bmt = new BlockMinerThread(this);
        bmt.start();
        logger.log("Started mining blockchain");
        sendMessageToServers("Started mining blockchain");
    }
}



