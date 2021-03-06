package ru.spbau.group202.sharkova.ftp.client;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.group202.sharkova.ftp.server.Server;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.NotADirectoryException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.UnableToSaveFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class test correctness of the Client class methods 'list' and 'get'.
 */
public class ClientTest {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    private List<FileEntry> expected = new ArrayList<>();

    private Client client;

    @BeforeClass
    public static void startSever() {
        Server server = new Server(PORT);
        server.start();
    }

    @Test
    public void listTest() throws FTPException, IOException, NotADirectoryException {
        initialize();

        List<FileEntry> actual = client.list(dir.getRoot().getAbsolutePath());
        assertTrue(actual.isEmpty());

        for (int i = 0; i < 3; i++) {
            createFile();
            createDir();
        }

        actual = client.list(dir.getRoot().getAbsolutePath());
        assertEquals(expected.size(), actual.size());
        for (FileEntry entry : expected) {
            assertTrue(actual.contains(entry));
        }
    }

    @Test(expected = NotADirectoryException.class)
    public void listNotADirectory() throws FTPException, IOException, NotADirectoryException {
        initialize();
        Path newFile = createFile();
        client.list(newFile.toString());
    }

    @Test(expected = UnableToSaveFileException.class)
    public void getFolderTest() throws IOException, FTPException, UnableToSaveFileException {
        initialize();
        Path newDir = createDir();
        client.get(newDir.toString());
    }


    @Test
    public void getEmptyFileTest() throws IOException, FTPException, UnableToSaveFileException {
        initialize();
        Path newFile = createFile();
        byte[] content = client.get(newFile.toString());
        assertEquals(0, content.length);
    }


    @Test
    public void getTest() throws IOException, FTPException, UnableToSaveFileException {
        initialize();
        Path newFile = createFile();
        byte[] content = {32, 14, 15, 78};
        Files.write(newFile, content);
        byte[] actual = client.get(newFile.toString());
        assertArrayEquals(content, actual);
    }


    // 'big' means 'bigger than the buffer size'
    @Test
    public void getBigFileTest() throws IOException, FTPException, UnableToSaveFileException {
        initialize();
        Path newFile = createFile();
        byte[] content = new byte[5000];
        for (int i = 0; i < 1000; i += 5) {
            content[i] = 32;
            content[i + 1] = 30;
            content[i + 2] = 40;
            content[i + 3] = 14;
            content[i + 4] = 89;
        }
        Files.write(newFile, content);
        byte[] actual = client.get(newFile.toString());
        assertArrayEquals(content, actual);
    }

    private void initialize() throws FTPException {
        client = new Client(HOST, PORT);
        client.connect();
    }

    private Path createDir() throws IOException {
        File newDir = dir.newFolder();
        expected.add(new FileEntry(newDir.getName(), true));
        return newDir.toPath();
    }

    private Path createFile() throws IOException {
        File newFile = dir.newFile();
        expected.add(new FileEntry(newFile.getName(), false));
        return newFile.toPath();
    }
}