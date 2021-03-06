package com.kkagurazaka.reactive.repository.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PrefsEntity {

    String preferencesName() default "";

    boolean useDefaultPreferences() default false;

    boolean commitOnSave() default false;

    Class<?> typeAdapter() default DEFAULT_ADAPTER.class;

    final class DEFAULT_ADAPTER {
    }
}
