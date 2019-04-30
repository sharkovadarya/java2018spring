package ru.spbau.group202.sharkova.hw1pool;

/**
 * This exception is thrown by LightFuture get() method
 * whenever an exception occurred during supplier get() method execution.
 */
public class LightExecutionException extends Exception {
    LightExecutionException(Exception e) {
        super(e);
    }
}
