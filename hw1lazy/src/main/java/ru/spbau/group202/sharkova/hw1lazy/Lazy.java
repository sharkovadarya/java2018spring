package ru.spbau.group202.sharkova.hw1lazy;

/**
 * An interface for getting a value
 * (from a supplier provided in implementations)
 * in a lazy way.
 * get() method works lazily: the returned value
 * should never be calculated twice.
 * @param <T> type of the return value
 */
public interface Lazy <T> {
	/**
	 * Gets a value (from the provided supplier) lazily.
	 * @return the supplier value
	 */
    T get();
}
