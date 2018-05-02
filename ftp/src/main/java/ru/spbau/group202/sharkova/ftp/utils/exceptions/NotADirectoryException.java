package ru.spbau.group202.sharkova.ftp.utils.exceptions;

/**
 * Exception that gets thrown when the path for the 'list' request
 * is not one of a directory.
 * Thrown on the client part.
 */
public class NotADirectoryException extends Exception {
    public NotADirectoryException(String message) {
        super(message);
    }
}
