package ru.spbau.group202.sharkova.ftp.server;

import ru.spbau.group202.sharkova.ftp.server.handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class implements a simple server
 * handling each client request in a separate thread.
 * Supported queries:
 * 'list' (list files in given directory);
 * 'get' (download file by filepath)
 */
public class Server {

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private boolean isRunning;

    private ServerSocket socket;

    public Server(int port) {
        this.port = port;
    }

    public static int getDefaultPort() {
        return DEFAULT_PORT;
    }

    /**
     * This method starts the server.
     */
    public void start() {
        try {
            socket = new ServerSocket(port);
            isRunning = true;
            Thread thread = new Thread(new ClientHandler(this));
            thread.start();
        } catch (IOException e) {
            //unable to connect
            return;
        }
    }

    /**
     * This method terminates the server.
     */
    public void finish() {
        isRunning = false;
    }

    /**
     * This method checks the state of the server.
     * @return true if the server is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        Server server = new Server(DEFAULT_PORT);
        server.start();
    }
}
