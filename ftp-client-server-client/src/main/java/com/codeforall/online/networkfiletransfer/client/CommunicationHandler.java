package com.codeforall.online.networkfiletransfer.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CommunicationHandler {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private static final String CLIENT_ROOT = new File("ftp-client-server-client/clientRoot").getAbsolutePath() + File.separator;

    public CommunicationHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void start() {
        try (Scanner sc = new Scanner(System.in)) {


            System.out.println(in.readUTF());
            System.out.println(in.readUTF());

            while (true) {
                System.out.print("Enter command: ");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) continue;

                out.writeUTF(input);
                out.flush();

                String cmd = input.split("\\s+")[0].toLowerCase();

                switch (cmd) {
                    case "get":
                        if (input.split("\\s+").length < 2) {
                            System.out.println("Usage: get <filename>");
                        } else {
                            downloadFile(input.split("\\s+", 2)[1]);
                        }
                        break;

                    case "put":
                        if (input.split("\\s+").length < 2) {
                            System.out.println("Usage: put <filename>");
                        } else {
                            uploadFile(input.split("\\s+", 2)[1]);
                        }
                        break;

                    case "list":
                        int count = in.readInt();
                        if (count == 0) System.out.println("No files found.");
                        else {
                            System.out.println("Files on server:");
                            for (int i = 0; i < count; i++) {
                                System.out.println(" - " + in.readUTF());
                            }
                        }
                        break;

                    case "quit":
                    case "bye":
                    case "disconnect":
                        System.out.println(in.readUTF());
                        return;

                    default:
                        System.out.println(in.readUTF());
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(String filename) throws IOException {
        long size = in.readLong();
        if (size == -1) {
            System.out.println("File not found: " + filename);
            return;
        }

        File file = new File(CLIENT_ROOT + filename);
        file.getParentFile().mkdirs(); // ensure nested dirs exist

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long totalRead = 0;
            int read;
            while (totalRead < size && (read = in.read(buffer)) != -1) {
                if (totalRead + read > size) read = (int) (size - totalRead);
                fos.write(buffer, 0, read);
                totalRead += read;
            }
        }

        System.out.println("Downloaded: " + filename + " (" + size + " bytes)");
    }

    private void uploadFile(String relativePath) throws IOException {

        File file = new File(CLIENT_ROOT + relativePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found locally: " + file.getAbsolutePath());

            out.writeLong(-1);
            out.flush();
            return;
        }


        out.writeLong(file.length());
        out.flush();


        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        out.flush();

        System.out.println("Uploaded: " + relativePath);
    }
}

// We have to either go with PrintWritter/BufferedReader combo or DataStreams for the same socket, otherwise caquinha
/*package com.codeforall.online.networkfiletransfer.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
The CommunicationHandler class manages the data exchange protocol between the client and server.
It handles both control signals (text commands) and data streams (binary file transfers).

public class CommunicationHandler {

    private final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final DataInputStream dataIn;

    /**
     Create a CommunicationHandler and initializes both text and binary streams.

    public CommunicationHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        // Wrapping the input stream for character-based reading (commands)
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // Wrapping the output stream for character-based writing (requests)
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        // Wrapping the same input stream for primitive/binary data reading
        this.dataIn = new DataInputStream(clientSocket.getInputStream());
    }

    /**
     Starts the main user-interface loop.
     Continuously listens for console input to send commands to the server.
     It specifically routes "get" commands to the binary download logic.

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {

            String welcomeLine;
            while ((welcomeLine = in.readLine()) != null) {
                System.out.println(welcomeLine);
                if (welcomeLine.contains("Enter your choice:")) {
                    break;
                }
            }
            System.out.println();
            while (true) {
                System.out.print("Enter command (get <filename> / list / quit): ");
                String message = scanner.nextLine();

                out.println(message);

                if (message.equalsIgnoreCase("quit")) break;

                if (message.startsWith("get ")) {
                    String fileName = message.substring(4);
                    downloadFile(fileName);
                } else if (message.equalsIgnoreCase("list")) {
                    // CHANGED: Read count first, then that many files
                    String countStr = in.readLine();
                    int count = Integer.parseInt(countStr);
                    if (count == 0) {
                        System.out.println("No files on server.");
                    } else {
                        System.out.println("Files on server:");
                        for (int i = 0; i < count; i++) {
                            String fileName = in.readLine();
                            System.out.println("  - " + fileName);
                        }
                    }
                } else {
                    String response = in.readLine();
                    if (response != null) {
                        System.out.println("Server: " + response);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Communication Error: " + e.getMessage());
        } finally {
            close();
        }
    }

    /**
     Manages the reception of a file over the network.
     It expects the server to send the file size as a long value first, followed by the raw bytes of the file.

    private void downloadFile(String fileName) {
        try {
            // Read the file size first (sent by the server as a long)
            // A value of -1 is used as a signal that the file does not exist
            long fileSize = dataIn.readLong();

            if (fileSize == -1) {
                System.out.println("File NOT FOUND on Server.");
                return;
            }

            // Prepare the local file to be written inside the clientRoot folder
            File file = new File("clientRoot/" + fileName);
            file.getParentFile().mkdirs();
            // Using try-with-resources to ensure the FileOutputStream is closed after writing
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096]; // 4KB buffer for efficient transfer
                int bytesRead;
                long totalRead = 0;

                // Read bytes until we reach the expected file size
                // We must check both bytesRead (for EOF) and totalRead (for protocol completion)
                while (totalRead < fileSize && (bytesRead = dataIn.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                System.out.println("Download Complete: " + fileName + " (" + totalRead + " bytes)");
            }
        } catch (IOException e) {
            System.err.println("Error Downloading File: " + e.getMessage());
        }
    }

    /**
     Closes the socket connection and releases resources.

    private void close() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/