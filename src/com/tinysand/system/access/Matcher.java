package com.tinysand.system.access;

public interface Matcher<R> {
    boolean match(final Object beanObject);

    R apply(final Object beanObject);
}
