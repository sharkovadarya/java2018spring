package ru.spbau.group202.sharkova.ftp.client;

public class FileEntry {
    private final String filename;
    private final boolean isDirectory;

    public FileEntry(String filename, boolean isDirectory) {
        this.filename = filename;
        this.isDirectory = isDirectory;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String toString() {
        return filename + (isDirectory ? " directory" : " file");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileEntry)) {
            return false;
        }

        FileEntry other = (FileEntry) o;
        return other.isDirectory == this.isDirectory && other.filename.equals(this.filename);
    }
}
