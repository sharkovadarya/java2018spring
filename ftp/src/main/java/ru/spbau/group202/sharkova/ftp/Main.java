package ru.spbau.group202.sharkova.ftp;

import ru.spbau.group202.sharkova.ftp.client.Client;
import ru.spbau.group202.sharkova.ftp.client.FileEntry;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.NotADirectoryException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.UnableToSaveFileException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * This class handles the console input/output of the application.
 * The application can process 'list' and 'get' commands:
 * 'list' lists the files in the given directory;
 * 'get' downloads the given file to the resources directory.
 */
public class Main {

    private static final String LIST = "list";
    private static final String GET = "get";
    private static final String EXIT = "exit";

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private static final String DOWNLOAD_DIRECTORY = "src/main/resources/files/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Client client = new Client(HOST, PORT);

        try {
            client.connect();
        } catch (FTPException e) {
            System.out.println(e.getMessage());
            return;
        }

        listCommands();

        while (true) {
            String command = scanner.next();
            try {
                switch (command) {
                    case LIST:
                        String directory = scanner.next();
                        List<FileEntry> records = client.list(directory);
                        System.out.println(records.size() +
                                " files/subdirectories in directory " + directory + ":\n");
                        records.forEach(System.out::println);
                        break;
                    case GET:
                        String file = scanner.next();
                        byte[] content = client.get(file);
                        FileOutputStream fos = new FileOutputStream(DOWNLOAD_DIRECTORY + new File(file).getName());
                        fos.write(content);
                        fos.close();
                        System.out.println("Saved file " + file + " in directory " + DOWNLOAD_DIRECTORY);
                        break;
                    case EXIT:
                        client.disconnect();
                        return;
                }
            } catch (UnableToSaveFileException|NotADirectoryException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Unable to save file");
            } catch (FTPException e) {
                System.out.println(e.getMessage());
                return;
            }

        }
    }

    private static void listCommands() {
        System.out.println("Commands:");
        System.out.println("'list + directory name' lists files in the given directory");
        System.out.println("'get + file name' downloads the given file to the resources directory");
        System.out.println("'exit' closes the connections and stops the program");
    }
}
