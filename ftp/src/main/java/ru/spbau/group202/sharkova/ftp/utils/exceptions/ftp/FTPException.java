package ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp;

/**
 * This is a general exception class for various exceptions
 * which might be thrown during the server/client working process.
 */
public class FTPException extends Exception {
    public FTPException(String message) {
        super(message);
    }
}
