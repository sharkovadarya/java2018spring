package ru.spbau.group202.sharkova.hw1pool;

import java.util.function.Function;

/**
 * This interface represents a task for a thread pool.
 * Available methods: check if the task is completed,
 * get task result, chain the execution result to another task.
 * @param <T> task execution result type parameter
 * */
public interface LightFuture<T> {
    /**
     * Indicates the state of the task.
     * @return true if the task is completed.
     */
    boolean isReady();

    /**
     * Returns task execution result;
     * if not completed, waits until the execution is finished.
     * @throws LightExecutionException if the corresponding supplier
     * throws an exception.
     * */
    T get() throws LightExecutionException;

    /**
     * Takes current task result and applies a given Function object
     * to it, creating a new task.
     * @param function the task that uses the current task result
     * @return a new task
     * */
    LightFuture<T> thenApply(Function<T, T> function);
}
