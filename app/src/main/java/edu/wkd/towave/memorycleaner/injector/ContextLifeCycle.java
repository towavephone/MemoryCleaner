package edu.wkd.towave.memorycleaner.injector;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier @Documented @Retention(RUNTIME) public @interface ContextLifeCycle {
    String value() default "App";
}
