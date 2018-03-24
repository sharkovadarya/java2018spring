package ru.spbau.group202.sharkova.hw1lazy;

import java.util.function.Supplier;

/**
 * Factory class that creates Lazy objects
 * providing the corresponding constructors with suppliers.
 */
public class LazyFactory {
	/**
	 * Creates a new LazyRegular object.
	 */	
    public static <T> Lazy<T> createLazyRegular(Supplier<T> supplier) {
        return new LazyRegular<>(supplier);
    }

    /**
     * Creates a new LazyConcurrent object.
     */
    public static<T> Lazy<T> createLazyConcurrent(Supplier<T> supplier) {
        return new LazyConcurrent<>(supplier);
    }
}
