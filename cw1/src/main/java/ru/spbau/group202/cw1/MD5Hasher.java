package ru.spbau.group202.cw1;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Instances of this abstract class calculate MD5 check-sum of a directory.
 */
abstract public class MD5Hasher {
    protected final static int BUFFER_SIZE = 4096;

    /**
     * This method calculates MD5 check-sum of a file or a directory.
     * @param path path to file/directory
     * @return byte[] array storing MD5 check-sum
     */
    @NotNull
    public abstract byte[] getHashFromPath(@NotNull Path path) throws IOException, NoSuchAlgorithmException;
}
