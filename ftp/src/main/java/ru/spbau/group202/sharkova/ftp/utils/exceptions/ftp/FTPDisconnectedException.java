package ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp;

/**
 * Exception which might be thrown while attempting to disconnect from server.
 */
public class FTPDisconnectedException extends FTPException {
    public FTPDisconnectedException(String message) {
        super(message);
    }
}
