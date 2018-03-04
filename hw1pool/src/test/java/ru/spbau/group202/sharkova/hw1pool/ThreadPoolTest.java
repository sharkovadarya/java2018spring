package ru.spbau.group202.sharkova.hw1pool;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * This class tests ThreadPool methods tests
 * and correctness of LightFuture methods implementation
 * used inside ThreadPool.
 */
public class ThreadPoolTest {

    private static final int numberOfIterations = 10;
    private static final int numberOfThreads = 10;

    @Test
    public void testBasicIntegerSupplier() throws LightExecutionException {
        ThreadPool<Integer> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Integer> lf = threadPool.add(() -> 16);
        assertEquals(new Integer(16), lf.get());
    }

    @Test
    public void testIncrementalSupplier() throws LightExecutionException {
        ThreadPool<Integer> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Integer> lf1 = threadPool.add(new IncrementalSupplier());
        assertEquals(new Integer(0), lf1.get());
        threadPool.shutdown();
        threadPool = new ThreadPool<>(1);
        LightFuture<Integer> lf2 = threadPool.add(new IncrementalSupplier());
        assertEquals(lf1.get(), lf2.get());
    }

    @Test
    public void testIncrementalSupplierMultipleQueries() throws LightExecutionException {
        ThreadPool<Integer> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Integer> lf = threadPool.add(new IncrementalSupplier());
        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(new Integer(0), lf.get());
        }
    }

    @Test
    public void testMultipleTasksMultipleQueries() throws LightExecutionException {
        ThreadPool<Integer> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Integer> lf1 = threadPool.add(() -> 20 * 30 * 40 - 50 + 60 - 70 + 80 * 90);
        LightFuture<Integer> lf2 = threadPool.add(() -> 31140);
        Supplier<Integer> supplier = new IncrementalSupplier();
        LightFuture<Integer> lf3 = threadPool.add(supplier);
        LightFuture<Integer> lf4 = threadPool.add(supplier);
        LightFuture<Integer> lf5 = threadPool.add(new IncrementalSupplier());

        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(lf2.get(), lf1.get());
        }

        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(new Integer(0), lf3.get());
        }

        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(lf3.get(), lf5.get());
        }

        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(new Integer(1), lf4.get());
        }
    }

    @Test
    public void testNullSupplier() throws LightExecutionException {
        ThreadPool<Object> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Object> lf1 = threadPool.add(() -> null);
        LightFuture<Object> lf2 = threadPool.add(Object::new);

        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(null, lf1.get());
        }

        for (int i = 0; i < numberOfIterations; i++) {
            assertNotNull(lf2.get());
        }
    }

    @Test
    public void testThenApplyInteger() throws LightExecutionException {
        ThreadPool<Integer> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Integer> lf1 = threadPool.add(() -> 30);
        LightFuture<Integer> lf2 = lf1.thenApply((x) -> (x + 2));
        assertEquals(new Integer(32), lf2.get());
    }

    @Test
    public void testThenApply() throws LightExecutionException {
        ThreadPool<Object> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Object> lf1 = threadPool.add(() -> {
            int res = 1;
            for (int i = 1; i <= 12; i++) {
                res *= i;
            }
            return res;
        });
        assertNotNull(lf1.get());
        LightFuture<Object> lf2 = lf1.thenApply((x) -> null);
        assertNull(lf2.get());
    }

    @Test
    public void testIsReady() throws LightExecutionException {
        ThreadPool<Object> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Object> lf1 = threadPool.add(Object::new);
        lf1.get();
        assertTrue(lf1.isReady());

        LightFuture<Object> lf2 = threadPool.add(() -> {
            while (true) {
                Thread.yield();
            }
        });
        for (int i = 0; i < numberOfIterations; i++) {
            assertFalse(lf2.isReady());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testShutdown() throws LightExecutionException, NoSuchFieldException, IllegalAccessException {
        ThreadPool<Object> threadPool = new ThreadPool<>(numberOfThreads);
        LightFuture<Object> lf = threadPool.add(() -> {
            while (true) {
                Thread.yield();
            }
        });

        Field threads = ThreadPool.class.getDeclaredField("threads");
        threads.setAccessible(true);
        for (Thread thread : (ArrayList<Thread>) threads.get(threadPool)) {
            assertFalse(thread.isInterrupted());
        }

        threadPool.shutdown();
        for (Thread thread : (ArrayList<Thread>) threads.get(threadPool)) {
            assertTrue(thread.isInterrupted());
        }
    }

    @Test(expected = LightExecutionException.class)
    public void testLightExecutionException() throws LightExecutionException {
        ThreadPool<Object> threadPool = new ThreadPool<>(numberOfThreads);
        threadPool.add(() -> {
            throw new NullPointerException();
        }).get();
    }

    private class IncrementalSupplier implements Supplier<Integer> {
        private int cnt = 0;
        @Override
        public Integer get() {
            return cnt++;
        }
    }

}