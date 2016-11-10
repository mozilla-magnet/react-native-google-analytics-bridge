package com.idehub.GoogleAnalyticsBridge;

import java.util.NoSuchElementException;

/**
 * Minimal and backwards compatible Optional<T> type, not fully compatible with Java 8
 * version
 */
class Optional<T> {

    private final T _value;

    private Optional(T value) {
        _value = value;
    }

    public static<T> Optional<T> of(T value) {
        if (value == null) {
            throw new NullPointerException("Cannot create Optional containing null");
        }

        return new Optional<>(value);
    }

    public static Optional<String> emptyString() {
      return new Optional<String>(null);
    }

    public static Optional<Integer> emptyInteger() {
      return new Optional<Integer>(null);
    }

    public static<T> Optional<T> ofNullable(T value) {
        return value == null ?
          // It is annoying we can't use a more structured/efficient approach
          // and leverage the type system to do inference. You can in Java 8,
          // but not 7. But then, Java 8 has an Optional type that does this
          // anyway see:
          // http://hg.openjdk.java.net/lambda/lambda/jdk/file/tip/src/share/classes/java/util/Optional.java#l49
          new Optional<T>(null) : of(value);
    }

    public boolean isPresent() {
        return _value != null;
    }

    public T get() {
        if (!this.isPresent()) {
            throw new NoSuchElementException("No value in Optional");
        }

        return _value;
    }
}
