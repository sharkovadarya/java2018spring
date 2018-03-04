package ru.spbau.group202.sharkova.hw1pool;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool implementation.
 * Uses a given number of threads to execute LightFuture tasks.
 * @param <T> task return type parameter
 */
public class ThreadPool<T> {
    private ArrayList<Thread> threads = new ArrayList<>();
    private final Queue<LightFutureTask> tasks = new ConcurrentLinkedQueue<>();

    public ThreadPool(int numberOfThreads) {
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(new ThreadPoolTask()));
            threads.get(i).setDaemon(true);
            threads.get(i).start();
        }
    }

    /**
     * Add a new task to the queue.
     * @param supplier provides a job to be executed
     * @return LightFuture task encapsulating the provided supplier
     */
    public LightFuture<T> add(Supplier<T> supplier) {
        LightFutureTask task = new LightFutureTask(supplier);

        synchronized (tasks) {
            tasks.add(task);
        }

        return task;
    }

    /**
     * Interrupts all threads in the pool.
     */
    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /**
     * Utility class implementing LightFuture interface.
     */
    private class LightFutureTask implements LightFuture<T>{
        private boolean isReady;
        private T result;
        private Exception exception;
        private Supplier<T> supplier;

        public LightFutureTask(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isReady() {
            return isReady;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get() throws LightExecutionException {
            while (!isReady()) {
                Thread.yield();
            }

            if (exception != null) {
                throw new LightExecutionException(exception);
            }

            return result;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LightFuture<T> thenApply(Function<T, T> function) {
            return add(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Utility class used for queue task executing.
     */
    private class ThreadPoolTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                LightFutureTask task = null;
                synchronized (ThreadPool.this) {
                    if (!tasks.isEmpty()) {
                        task = tasks.poll();
                    }
                }

                if (task != null) {
                    try {
                        task.result = task.supplier.get();
                    } catch (Exception e) {
                        task.exception = e;
                    }

                    task.isReady = true;
                }
            }
        }
    }
}
