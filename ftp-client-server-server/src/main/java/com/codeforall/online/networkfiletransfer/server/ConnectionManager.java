package com.codeforall.online.networkfiletransfer.server;

import java.io.*;
import java.net.Socket;

public class ConnectionManager {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ConnectionManager(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    public String receiveMessage() throws IOException {
        return in.readUTF();
    }

    public void sendInt(int value) throws IOException {
        out.writeInt(value);
        out.flush();
    }

    public void sendFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            out.writeLong(-1);
            out.flush();
            return;
        }

        long size = file.length();
        out.writeLong(size);
        out.flush();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        out.flush();
    }

    public long receiveFileSize() throws IOException {
        return in.readLong();
    }

    public int readBytes(byte[] buffer) throws IOException {
        return in.read(buffer);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}



// Implementation for text files only
    /*public void sendFile(File fileName) throws IOException {
        String content = Files.readString(fileName.toPath());
        String[] lines = content.split("\n", -1);
        sendMessage("LINES:" + lines.length);

        for (String line : lines) {
            sendMessage(line);
        }
    }

    public void receiveFile(File filename) throws IOException {

        String message = receiveMessage();
        int numLines = Integer.parseInt(message.split(":")[1]);


        StringBuilder content = new StringBuilder();
        for (int i = 0; i < numLines; i++) {
            content.append(receiveMessage());
            if (i < numLines - 1) {
                content.append("\n");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content.toString());
        }

        System.out.println("File saved: " + filename);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();

    }
}
*/

