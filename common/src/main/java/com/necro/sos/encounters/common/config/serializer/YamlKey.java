package com.necro.sos.encounters.common.config.serializer;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface YamlKey {
    String value();
}