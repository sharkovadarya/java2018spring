package ru.spbau.group202.cw1;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * This implementation of MD5Hasher abstract class uses a ForkJoinPool
 * to compute MD5 check-sum.
 */
public class MD5MultiThread extends MD5Hasher {

    private ForkJoinPool pool;

    public MD5MultiThread(int numberOfThreads) {
        pool = new ForkJoinPool(numberOfThreads);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public byte[] getHashFromPath(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        if (Files.isDirectory(path)) {
            processDirectory(path, md5);
        } else {
            processFile(path, md5);
        }

        return md5.digest();
    }

    private void processDirectory(Path path, MessageDigest md) throws IOException {
        md.update(path.getFileName().toString().getBytes());
        ArrayList<RecursiveTask<byte[]>> tasks = new ArrayList<>();
        for (Path p : Files.walk(path).filter(Files::isRegularFile).collect(Collectors.toList())) {
            RecursiveTask<byte[]> task = new RecursiveTask<byte[]>() {
                @Override
                protected byte[] compute() {
                    try {
                        return getHashFromPath(p);
                    } catch (Exception e) {
                    }
                    return null;
                }
            };
            tasks.add(task);
            pool.execute(task);
        }

        for (RecursiveTask<byte[]> task : tasks) {
            try {
                byte[] hash = task.get();
                if (hash == null) {
                    throw new IOException("Unable to compute");
                }
                md.update(hash);
            } catch (Exception e) {
                throw new IOException("ForkJoinPool exception; unable to compute.");
            }
        }
    }

    private void processFile(Path path, MessageDigest md) throws IOException {
        try (DigestInputStream stream = new DigestInputStream(Files.newInputStream(path), md)) {
            byte[] buf = new byte[BUFFER_SIZE];
            while (stream.read(buf) != -1) {
                ;
            }
        }
    }


}
