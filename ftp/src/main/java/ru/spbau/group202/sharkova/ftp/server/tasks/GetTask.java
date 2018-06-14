package ru.spbau.group202.sharkova.ftp.server.tasks;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class implements the server part of the 'get' task.
 */
public class GetTask implements Runnable {

    private DataOutputStream clientInput;
    private String fileName;

    public GetTask(@NotNull DataOutputStream clientInput, @NotNull String fileName) {
        this.clientInput = clientInput;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                clientInput.writeLong(0);
                return;
            }

            if (file.isDirectory()) {
                clientInput.writeLong(-1);
                return;
            }

            Path filePath = Paths.get(fileName);
            clientInput.writeLong(file.length());
            Files.copy(filePath, clientInput);
        } catch (IOException e) {
            return;
        }
    }
}
