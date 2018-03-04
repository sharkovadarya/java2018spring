package ru.spbau.group202.sharkova.hw1lazy;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Lazy<T> interface implementation which is not intended
 * for multithread usage.
 */
public class LazyRegular<T> extends AbstractLazy<T> {

    private Object value = noValue;

    public LazyRegular(Supplier<T> supplier) {
        super(supplier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable
    T get() {
        if (value == noValue) {
            value = supplier.get();
        }

        return (T) value;
    }
}
