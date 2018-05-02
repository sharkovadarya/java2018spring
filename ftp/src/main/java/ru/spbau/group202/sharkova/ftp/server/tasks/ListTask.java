package ru.spbau.group202.sharkova.ftp.server.tasks;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * This class implements the server part of the 'list' task.
 */
public class ListTask implements Runnable {

    private DataOutputStream clientInput;
    private String directoryName;

    public ListTask(@NotNull DataOutputStream clientInput, @NotNull String directoryName) {
        this.clientInput = clientInput;
        this.directoryName = directoryName;
    }

    @Override
    public void run() {
        try {
            File dir = new File(directoryName);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files == null) {
                    return;
                }
                clientInput.writeInt(files.length);
                for (File f : files) {
                    clientInput.writeUTF(f.getName());
                    clientInput.writeBoolean(f.isDirectory());
                }
            } else {
                clientInput.writeInt(-1);
            }

        } catch (IOException e) {
            return;
        }
    }
}
