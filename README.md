Vanilla FTP — Java Client-Server File Transfer

A custom implementation of the File Transfer Protocol (FTP) built from scratch in Java, using raw TCP sockets. This project was built as a practical exercise in networked application development.

 What It Does

A server and client application that lets users upload and download files over a TCP connection. The server runs continuously and handles one client session at a time, supporting the following commands:

| Command      | Description                        |
|--------------|------------------------------------|
| `LS`         | List files available on the server |
| `GET <file>` | Download a file from the server    |
| `PUT <file>` | Upload a file to the server        |
| `MKDIR <dir>`| Create a directory on the server   |
| `HELP`       | Show available commands            |
| `BYE` / `DISCONNECT` / `QUIT` | End the session   |

Project Structure

Two independent Maven projects, each packaged as a JAR:

- **`ftp-server`** — listens for connections and serves files from `serverRoot/`
- **`ftp-client`** — connects to the server and stores downloaded files in `clientRoot/`

Key Concepts Practiced

- **Client-server model** over TCP using `java.net.Socket` and `ServerSocket`
- **Bidirectional I/O streams** for control messages and binary file transfer
- **Graceful error handling** and connection teardown
- **Maven project structure** and packaging with the Assembly Plugin

Running the App

Build both projects with:
```bash
mvn package
```

Start the server:
```bash
java -jar ftp-server/target/ftp-server-jar-with-dependencies.jar
```

Start the client in a separate terminal:
```bash
java -jar ftp-client/target/ftp-client-jar-with-dependencies.jar
```
