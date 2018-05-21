package com.tzj.parsercombinators.core;

import java.util.Objects;

/**
 * A custom pair type.
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
    T first;
    U second;

    public Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public U second() {
        return second;
    }

    @Override
    public String toString() {
        return "( " + first + ", " + second + " )";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
