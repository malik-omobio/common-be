package com.omobio.springbase.util.abst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guards a controller method behind one or more permission keys.
 * Apps define their own permission constants (see CorePermissions for the built-in ones).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Guard {
    String[] value();
}
