package ru.spbau.group202.sharkova.ftp.server.handlers;

import org.jetbrains.annotations.NotNull;
import ru.spbau.group202.sharkova.ftp.server.Server;

import java.io.IOException;
import java.net.Socket;

/**
 * This class handles clients that attempt to connect to the server.
 */
public class ClientHandler implements Runnable {

    private Server server;

    public ClientHandler(@NotNull Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            Socket connection;
            try {
                connection = server.getSocket().accept();
            } catch (IOException e) {
                server.finish();
                return;
            }
            Thread thread = new Thread(new ConnectionHandler(server, connection));
            thread.start();
        }
    }
}
