package com.codeforall.online.networkfiletransfer.client;

import java.io.IOException;
import java.net.Socket;

/**
 The Client class is responsible for establishing the initial network connection.
 It acts as the bridge between the configuration (provided by ClientGenerator) and the logic (managed by CommunicationHandler).
 This class follows the Single Responsibility Principle by only handling the socket connection setup.
 */
public class Client {

    private final String host;
    private final int port;

    /**
     Initializes a new Client instance with server details.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     Attempts to connect to the server and delegates the communication flow to the CommunicationHandler.
     If the connection fails, an error message is printed to the standard error stream.
     */
    public void start() {
        try {
            // Establish Connection with Server
            // This creates a TCP handshake between this client and the server
            Socket clientSocket = new Socket(host, port);
            System.out.println("Connected to Server at :" + host + "&" + port);

            // Dependency Injection: Pass the active clientSocket to the CommunicationHandler.
            // This allows the handler to use the socket's input and output streams.
            CommunicationHandler handler = new CommunicationHandler(clientSocket);

            // Hand over the thread execution to the handler's logic
            handler.start();

        } catch (IOException e) {
            // Catching IOException covers both UnknownHostException and connection refusal
            System.err.println("Impossible to Connect to Server: " + e.getMessage());
        }
    }
}