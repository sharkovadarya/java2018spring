package ru.spbau.group202.sharkova.hw1lazy;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * LazyFactory test class.
 */
public class LazyFactoryTest {

    private final Supplier<Integer> incrementalSupplier = new IncrementalSupplier();
    private final Supplier<BigDecimal> factorial1000 = () -> {
        BigDecimal fact = BigDecimal.ONE;
        for (int i = 1; i <= 10000; ++i) {
            fact = fact.multiply(new BigDecimal(i));
        }

        return fact;
    };

    private static final int numberOfThreads = 15;
    private static final int numberOfIterations = 100;

    @Test
    public void testLazyRegularInteger() {
        Lazy<Integer> lazyInteger = LazyFactory.createLazyRegular(() -> 5);
        testLazyBasicUsage(lazyInteger, 5);
    }

    @Test
    public void testLazyRegularNull() {
        Lazy<Object> lazyObject = LazyFactory.createLazyRegular(() -> null);
        testLazyNull(lazyObject);
    }

    @Test
    public void testLazyRegularIncremental() {
        Lazy<Integer> lazyInteger = LazyFactory.createLazyRegular(incrementalSupplier);
        testLazyIncremental(lazyInteger);
    }

    @Test
    public void testLazyConcurrentIntegerRegularUsage() {
        Lazy<Integer> lazyInterger = LazyFactory.createLazyConcurrent(() -> 30);
        testLazyBasicUsage(lazyInterger, 30);
    }

    @Test
    public void testLazyConcurrentNullRegularUsage() {
        Lazy<Object> lazyObject = LazyFactory.createLazyConcurrent(() -> null);
        testLazyNull(lazyObject);
    }

    @Test
    public void testLazyConcurrentIncrementalRegularUsage() {
        Lazy<Integer> lazyInteger = LazyFactory.createLazyConcurrent(incrementalSupplier);
        testLazyIncremental(lazyInteger);
    }

    @Test
    public void testLazyConcurrentIntegerMultithreadUsage() throws InterruptedException {
        Lazy<Integer> lazyInteger = LazyFactory.createLazyConcurrent(() -> 5);
        testLazyBasicUsageMultithread(lazyInteger, () -> 5);
    }

    @Test
    public void testLazyConcurrentNullMultithreadUsage() throws InterruptedException {
        Lazy<Object> lazyObject = LazyFactory.createLazyConcurrent(() -> null);
        testLazyBasicUsageMultithread(lazyObject, () -> null);
    }

    @Test
    public void testLazyConcurrentIncrementalMultithreadUsage() throws InterruptedException {
        Lazy<Integer> lazyInteger = LazyFactory.createLazyConcurrent(new IncrementalSupplier());
        testLazyBasicUsageMultithread(lazyInteger, new IncrementalSupplier());
    }

    @Test
    public void testLazyConcurrentFactorialMultithreadUsage() throws InterruptedException {
        Lazy<BigDecimal> lazyBigDecimal = LazyFactory.createLazyConcurrent(factorial1000);
        testLazyBasicUsageMultithread(lazyBigDecimal, factorial1000);
    }

    private <T> void testLazyBasicUsage(Lazy<T> lazy, T expectedValue) {
        final T res = lazy.get();
        assertEquals(expectedValue, res);
        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(lazy.get(), res);
        }
    }

    private void testLazyNull(Lazy<Object> lazyObject) {
        assertEquals(null, lazyObject.get());
        // double check
        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(null, lazyObject.get());
        }
    }

    private void testLazyIncremental(Lazy<Integer> lazyInteger) {
        // this value should not increase
        for (int i = 0; i < numberOfIterations; i++) {
            assertEquals(new Integer(0), lazyInteger.get());
        }

        // this value should increase
        assertEquals(new Integer(1), incrementalSupplier.get());
        assertEquals(new Integer(2), incrementalSupplier.get());
    }

    private <T> void testLazyBasicUsageMultithread(Lazy<T> lazy, Supplier<T> supplier)
            throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        T expectedValue = supplier.get();
        Runnable runnable = () -> testLazyBasicUsage(lazy, expectedValue);

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    private class IncrementalSupplier implements Supplier<Integer> {
        private int cnt = 0;

        @Override
        public Integer get() {
            return cnt++;
        }
    };
}