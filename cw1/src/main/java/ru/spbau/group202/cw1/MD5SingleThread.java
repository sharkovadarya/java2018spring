package ru.spbau.group202.cw1;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

/**
 * This implementation of MD5Hasher abstract class calculates MD5 check-sum
 * in a regular way, using a single thread.
 */
public class MD5SingleThread extends MD5Hasher {

    /**
     * {@inheritDoc}
     */
    @NotNull
    public byte[] getHashFromPath(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        if (Files.isDirectory(path)) {
            md5.update(path.getFileName().toString().getBytes());
            for (Path p : Files.walk(path).filter(Files::isRegularFile).collect(Collectors.toList())) {
                md5.update(getHashFromPath(p));
            }

            return md5.digest();
        }

        try (DigestInputStream stream = new DigestInputStream(Files.newInputStream(path), md5)) {
            byte[] buf = new byte[BUFFER_SIZE];
            while (stream.read(buf) != -1) {
                ;
            }
            return stream.getMessageDigest().digest();
        }
    }
}
