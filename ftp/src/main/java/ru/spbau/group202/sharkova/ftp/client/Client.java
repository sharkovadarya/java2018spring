package ru.spbau.group202.sharkova.ftp.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.*;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPConnectionException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPDisconnectedException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple client implementation. This client can connect to/disconnect from the server
 * and execute 'list' and 'get' commands.
 */
public class Client {
    private static final int LIST_COMMAND = 1;
    private static final int GET_COMMAND = 2;

    private static final int BUFFER_SIZE = 4096;

    private final String host;
    private final int port;

    private Socket serverSocket;

    private DataInputStream outputForServer;
    private DataOutputStream inputForServer;

    public Client(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * This method connects the client to the server in use.
     * @throws FTPConnectionException if the connection could not be established
     */
    public void connect() throws FTPConnectionException {
        try {
            serverSocket = new Socket(host, port);
            outputForServer = new DataInputStream(serverSocket.getInputStream());
            inputForServer = new DataOutputStream(serverSocket.getOutputStream());
        } catch (IOException e) {
            throw new FTPConnectionException("Unable to establish connection.\n" + e.getMessage());
        }
    }

    /**
     * This method disconnects the client from the server in use.
     * @throws FTPDisconnectedException while attempting to close the connection
     */
    public void disconnect() throws FTPDisconnectedException {
        try {
            inputForServer.close();
            outputForServer.close();
            serverSocket.close();
        } catch (IOException e) {
            throw new FTPDisconnectedException(e.getMessage());
        }
    }

    /**
     * This method handles the client part of the 'list' request.
     * @param directory the directory which files are listed from
     * @return list of directory files descriptions
     * @throws FTPException if there are problems with the server connection
     * @throws NotADirectoryException if the 'directory' param path is not one of a directory
     */
    public List<String> list(@NotNull String directory)
            throws FTPException, NotADirectoryException {
        try {
            inputForServer.writeInt(LIST_COMMAND);
            inputForServer.writeUTF(directory);
            inputForServer.flush();

            ArrayList<String> records = new ArrayList<>();
            int size = outputForServer.readInt();
            if (size == -1) {
                throw new NotADirectoryException("Provided path is not one of a directory.");
            }
            for (int i = 0; i < size; i++) {
                String name = outputForServer.readUTF();
                boolean isDir = outputForServer.readBoolean();
                records.add(name + " " + (isDir ? "directory" : "file"));
            }

            return records;

        } catch (IOException e) {
            disconnect();
            throw new FTPConnectionException("Unable to retrieve information from server.");
        }
    }

    /**
     * This method handles the client part of the 'get' request.
     * It reads a file from server and writes it to the given directory.
     * @param filename name of the file to be downloaded
     * @param pathToSave directory which the file is downloaded to
     * @throws FTPException if there are problems with the server connection
     * @throws UnableToSaveFileException if the given file was nonexistent or a directory
     */
    public void get(@NotNull String filename, @NotNull String pathToSave)
            throws FTPException, UnableToSaveFileException {
        try {
            inputForServer.writeInt(GET_COMMAND);
            inputForServer.writeUTF(filename);
            inputForServer.flush();

            long size = outputForServer.readLong();
            if (size == -1) {
                throw new UnableToSaveFileException("The file is a directory");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            long cur = 0;
            while (cur < size) {
                int readSize = outputForServer.read(buffer, 0,
                        size - cur > BUFFER_SIZE ? BUFFER_SIZE : (int) (size - cur));
                baos.write(buffer, 0, readSize);
                cur += readSize;
            }

            FileOutputStream fos = new FileOutputStream(pathToSave + new File(filename).getName());
            baos.writeTo(fos);
            fos.close();
            baos.close();
        } catch (IOException e) {
            disconnect();
            throw new FTPConnectionException("Unable to retrieve information from server.");
        }
    }

}
