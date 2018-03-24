package ru.spbau.group202.sharkova.hw1lazy;

import java.util.function.Supplier;

/**
 * Lazy interface abstract implementation
 * in order to avoid code duplication.
 */
abstract public class AbstractLazy<T> implements Lazy<T> {
    protected static final Object NO_VALUE = new Object();

    protected Supplier<T> supplier;

    public AbstractLazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }
}
