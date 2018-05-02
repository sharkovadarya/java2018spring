package ru.spbau.group202.sharkova.ftp.server.handlers;

import org.jetbrains.annotations.NotNull;
import ru.spbau.group202.sharkova.ftp.server.Server;
import ru.spbau.group202.sharkova.ftp.server.tasks.GetTask;
import ru.spbau.group202.sharkova.ftp.server.tasks.ListTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class handles the client in a separate thread and calls for a list/get task
 * to be executed.
 */
public class ConnectionHandler implements Runnable {

    private Server server;
    private Socket clientSocket;

    public ConnectionHandler(@NotNull Server server, @NotNull Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream clientOutput;
        DataOutputStream clientInput;

        try {
            clientInput = new DataOutputStream(clientSocket.getOutputStream());
            clientOutput = new DataInputStream(clientSocket.getInputStream());

            while (server.isRunning()) {
                int request;
                try {
                    request = clientOutput.readInt();
                } catch (Exception e) {
                    return;
                }

                switch (request) {
                    case 1:
                        Thread listTaskThread = new Thread(
                                new ListTask(clientInput, clientOutput.readUTF()));
                        listTaskThread.start();
                        break;
                    case 2:
                        Thread getTaskThread = new Thread(
                                new GetTask(clientInput, clientOutput.readUTF()));
                        getTaskThread.start();
                        break;
                    default:
                        return;
                }
            }
        } catch (IOException e) {
            return;
        }
    }
}
