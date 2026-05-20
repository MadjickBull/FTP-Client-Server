package com.codeforall.online.networkfiletransfer.client;

/**
The ClientGenerator class serves as the application launcher.
It's primary responsibility is to configure the connection parameters and bootstrap the Client instance.
This class isolates the configuration (HOST and PORT) from the networking logic.
*/
public class ClientGenerator {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) {

        // Instantiate the Client with the defined network settings.
        Client client = new Client(HOST, PORT);

        // Triggers the connection and communication process.
        client.start();
    }
}