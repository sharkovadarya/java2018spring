package ru.spbau.group202.sharkova.ftp.server;

import ru.spbau.group202.sharkova.ftp.server.handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * This class implements a simple server
 * handling each client request in a separate thread.
 * Supported queries:
 * 'list' (list files in given directory);
 * 'get' (download file by filepath)
 */
public class Server {

    private static final int DEFAULT_PORT = 8080;

    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String EXIT = "exit";

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

        System.out.println("Input 'start' to start server, 'stop' to stop server, 'exit' to exit program.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            switch (command) {
                case START:
                    if (server.isRunning()) {
                        System.out.println("The server is already running.");
                    } else {
                        server.start();
                        System.out.println("Started server.");
                    }
                    break;
                case STOP:
                    if (!server.isRunning()) {
                        System.out.println("The server is not running.");
                    } else {
                        server.finish();
                        System.out.println("Stopped server.");
                    }
                    break;
                case EXIT:
                    if (server.isRunning()) {
                        server.finish();
                    }
                    System.exit(0);
                default:
                    System.out.println("Unknown command.");
            }
        }

    }
}
