package ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp;

/**
 * Exception which gets thrown whenever connection problems occur.
 */
public class FTPConnectionException extends FTPException {
    public FTPConnectionException(String message) {
        super(message);
    }
}
