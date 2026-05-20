package com.codeforall.online.networkfiletransfer.server;

import com.codeforall.online.networkfiletransfer.server.commands.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommandHandler {

    private final ConnectionManager cm;
    private boolean running = true;

    private static final String SERVER_ROOT = new File("ftp-client-server-server/serverRoot").getAbsolutePath() + File.separator;

    public CommandHandler(ConnectionManager cm) {
        this.cm = cm;
    }

    public void start() throws IOException {
        sendWelcome();

        while (running) {
            String request;
            try {
                request = cm.receiveMessage();
            } catch (IOException e) {
                break;
            }
            if (request == null) break;
            handleCommand(request);
        }

        cm.close();
    }

    private void sendWelcome() throws IOException {
        String welcome = "Welcome to Filipe & Maria's File Transfer Server";
        cm.sendMessage(welcome);

        String menu = "Available commands: LIST, GET <filename>, PUT <filename>, MKDIR <dir>, HELP, QUIT/BYE/DISCONNECT";
        cm.sendMessage(menu);
    }

    private void handleCommand(String request) throws IOException {
        String[] parts = request.trim().split("\\s+", 2);
        Command cmd = Command.fromString(parts[0]);

        if (cmd == null) {
            cm.sendMessage("Invalid command");
            return;
        }

        switch (cmd) {
            case LIST:
                String subdir = parts.length > 1 ? parts[1] : "";
                listFiles(subdir);
                break;

            case GET:
                if (parts.length < 2) {
                    cm.sendMessage("Missing filename for GET");
                    break;
                }
                File fileToSend = new File(SERVER_ROOT + parts[1]);
                cm.sendFile(fileToSend);
                break;

            case PUT:
                if (parts.length < 2) {
                    cm.sendMessage("Usage: put <filename>");
                    break;
                }
                receiveFile(parts[1]);
                break;

            case MKDIR:
                if (parts.length < 2) {
                    cm.sendMessage("Usage: mkdir <dirname>");
                    break;
                }
                File dir = new File(SERVER_ROOT + parts[1]);
                if (dir.exists()) cm.sendMessage("Directory exists");
                else if (dir.mkdirs()) cm.sendMessage("Directory created");
                else cm.sendMessage("Failed to create directory");
                break;

            case HELP:
                sendWelcome();
                break;

            case QUIT:
            case BYE:
            case DISCONNECT:
                running = false;
                cm.sendMessage("Goodbye!");
                break;
        }
    }

    private void listFiles(String relativePath) throws IOException {
        File folder = new File(SERVER_ROOT + relativePath);
        if (!folder.exists() || !folder.isDirectory()) {
            cm.sendInt(0);
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            cm.sendInt(0);
            return;
        }

        cm.sendInt(files.length);
        for (File f : files) {
            String type = f.isDirectory() ? "[DIR] " : "[FILE]";
            cm.sendMessage(type + f.getName());
        }
    }

    private void receiveFile(String fileName) throws IOException {
        File file = new File(SERVER_ROOT + fileName);
        file.getParentFile().mkdirs();


        long size = cm.receiveFileSize();
        if (size == -1) {
            cm.sendMessage("Upload canceled: file missing on client");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long totalRead = 0;
            int read;
            while (totalRead < size && (read = cm.readBytes(buffer)) != -1) {
                if (totalRead + read > size) read = (int) (size - totalRead);
                fos.write(buffer, 0, read);
                totalRead += read;
            }
        }

        cm.sendMessage("Upload complete: " + fileName);
    }
}


/*
        /*public void download() throws IOException {
        connectionManager.sendMessage("Which file do you want to download?");
        String requestedFile = connectionManager.receiveMessage();

        File[] files = new File("serverRoot/").listFiles();
        for (File file : files) {

            if(file.getName().equals(requestedFile)){
                connectionManager.sendFile(file);
            }

        }
    }*/
    /*public void upload() throws IOException {
        connectionManager.sendMessage("Which file do you want to upload?");
        String fileName = connectionManager.receiveMessage();

        File chosenFile = new File("serverRoot/" + fileName);

        connectionManager.receiveFile(chosenFile);


    }*/