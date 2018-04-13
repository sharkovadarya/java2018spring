package ru.spbau.group202.cw1;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

// IMPORTANT: neither of those tests pass because of incorrect paths
// but all of them have passed manual testing (Main with args)

/**
 * This class tests correctness of MD5Hasher implementations methods
 */
public class MD5HasherTest {

    private final static int numberOfThreads = 4;

    private void testEmptyDir(MD5Hasher hasher) throws NoSuchAlgorithmException, IOException {
        // TODO this is currently not working
        // but this is how I'd implement the tests if I had time to figure out
        // how to get this test to believe the provided path exists
        Path path = Paths.get("/src/test/resources/testdir/dir");
        byte[] hash = hasher.getHashFromPath(path);
        String str = DatatypeConverter.printHexBinary(hash);
        // this md5 checksum was calculated here: http://progs.be/md5.html
        assertEquals("736007832d2167baaae763fd3a3f3cf1", str.toLowerCase());
    }

    private void testEmptyFile(MD5Hasher hasher) throws NoSuchAlgorithmException, IOException {
        Path path = Paths.get(this.getClass().getResource("/testdir/file2.txt").toString());
        byte[] hash = hasher.getHashFromPath(path);
        String str = DatatypeConverter.printHexBinary(hash);
        // this md5 checksum was calculated here: http://onlinemd5.com/
        assertEquals("D41D8CD98F00B204E9800998ECF8427E", str);
    }

    private void testNonEmptyFile(MD5Hasher hasher) throws NoSuchAlgorithmException, IOException {
        Path path = Paths.get(this.getClass().getResource("/testdir/file1.txt").toString());
        byte[] hash = hasher.getHashFromPath(path);
        String str = DatatypeConverter.printHexBinary(hash);
        // this md5 checksum was calculated here: http://onlinemd5.com/
        assertEquals("40A5D58FFA6E88AA578D6683AC413105", str);
    }

    @Test
    public void testEmptyDirRegularHasher() throws NoSuchAlgorithmException, IOException {
        MD5SingleThread hasher = new MD5SingleThread();
        testEmptyDir(hasher);
    }

    @Test
    public void testEmptyDirConcurrentHasher() throws NoSuchAlgorithmException, IOException {
        MD5MultiThread hasher = new MD5MultiThread(numberOfThreads);
        testEmptyDir(hasher);
    }

    @Test
    public void testEmptyFileRegularHasher() throws NoSuchAlgorithmException, IOException {
        MD5SingleThread hasher = new MD5SingleThread();
        testEmptyFile(hasher);
    }

    @Test
    public void testEmptyFileConcurrentHasher() throws NoSuchAlgorithmException, IOException {
        MD5MultiThread hasher = new MD5MultiThread(numberOfThreads);
        testEmptyFile(hasher);
    }

    @Test
    public void testNonEmptyFileRegularHasher() throws NoSuchAlgorithmException, IOException {
        MD5SingleThread hasher = new MD5SingleThread();
        testNonEmptyFile(hasher);
    }

    @Test
    public void testNonEmptyFileConcurrentHasher() throws NoSuchAlgorithmException, IOException {
        MD5MultiThread hasher = new MD5MultiThread(numberOfThreads);
        testNonEmptyFile(hasher);
    }

    @Test
    public void testEqualResultsSingleAndMultiThread() throws NoSuchAlgorithmException, IOException {
        MD5SingleThread regularHasher = new MD5SingleThread();
        MD5MultiThread concurrentHasher = new MD5MultiThread(numberOfThreads);

        Path path = Paths.get("/src/test/resources/testdir");
        byte[] hash = regularHasher.getHashFromPath(path);
        String str1 = DatatypeConverter.printHexBinary(hash);
        hash = concurrentHasher.getHashFromPath(path);
        String str2 = DatatypeConverter.printHexBinary(hash);
        assertEquals(str1, str2);
    }
}