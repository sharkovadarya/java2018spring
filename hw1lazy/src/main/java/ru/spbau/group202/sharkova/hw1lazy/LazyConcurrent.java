package ru.spbau.group202.sharkova.hw1lazy;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Lazy<T> interface implementation which is intended for multithread usage.
 */
public class LazyConcurrent<T> extends AbstractLazy<T> {

    private Object value = noValue;

    public LazyConcurrent(Supplier<T> supplier) {
        super(supplier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable
    T get() {
        // to avoid unnecessary blocking
        if (value != noValue) {
            return (T) value;
        }

        synchronized (this) {
            if (value == noValue) {
                value = supplier.get();
            }
        }

        return (T) value;
    }
}
