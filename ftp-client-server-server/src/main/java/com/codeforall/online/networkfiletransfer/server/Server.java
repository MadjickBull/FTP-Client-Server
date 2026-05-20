package com.codeforall.online.networkfiletransfer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    private static final int port = 1234;
    private Socket clientSocket;
    private final ServerSocket serverSocket;
    private boolean isRunning;

    public Server() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server initialized on port: " + port);
        System.out.println("Listening...");
        isRunning = false;
    }

    public void start() throws IOException {
        isRunning = true;
        System.out.println("Server is listening...");

        while (isRunning) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                ConnectionManager connectionManager = new ConnectionManager(clientSocket);
                CommandHandler commandHandler = new CommandHandler(connectionManager);
                commandHandler.start();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void stop () throws IOException {
        serverSocket.close();
        System.out.println("Server Stopped!");
    }
}