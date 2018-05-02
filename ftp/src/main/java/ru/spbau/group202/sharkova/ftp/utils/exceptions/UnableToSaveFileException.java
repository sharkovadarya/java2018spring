package ru.spbau.group202.sharkova.ftp.utils.exceptions;

/**
 * Exception that gets thrown during the 'get' request handling on the client part
 * when the file could not be downloaded.
 */
public class UnableToSaveFileException extends Exception {
    public UnableToSaveFileException(String message) {
        super(message);
    }
}
