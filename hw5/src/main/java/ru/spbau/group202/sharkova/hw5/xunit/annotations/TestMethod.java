package ru.spbau.group202.sharkova.hw5.xunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestMethod {
    String ignore()
    default "";

    Class<? extends Exception> expected()
    default NoExceptionExpected.class;

    // fake class because something needs to be returned from default implementation of expected()
    class NoExceptionExpected extends Exception {

    }
}
