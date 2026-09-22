package com.github.fbascheper.dj.console.bootstrap.hibernate;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.AbstractJsonFormatMapper;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;

/**
 * Jackson 3 (tools.jackson.*) based FormatMapper for Hibernate ORM 7 JSON types.
 * See Hibernate discussion about Jackson 3 + Hibernate 7: provide a custom FormatMapper.
 */
public final class CustomJacksonJsonFormatMapper extends AbstractJsonFormatMapper {

    private final JsonMapper mapper;

    /** Required no-arg constructor for Hibernate. */
    public CustomJacksonJsonFormatMapper() {
        // In Boot 4, Jackson 3's JsonMapper already supports Java Time out-of-the-box.
        // Lenient unknown properties: legacy JSONB rows without CrowdEvent.type may still carry extra fields.
        this(
                JsonMapper.builder()
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .build());
    }

    /** Optional: allow injecting a preconfigured JsonMapper. */
    public CustomJacksonJsonFormatMapper(JsonMapper mapper) {
        this.mapper = mapper;
    }

    /** Stream-based write used by Hibernate when binding JDBC JSON parameters. */
    @Override
    public <T> void writeToTarget(T value,
                                  JavaType<T> javaType,
                                  Object target,
                                  WrapperOptions options) {
        try {
            mapper.writerFor(mapper.constructType(javaType.getJavaType()))
                    .writeValue((JsonGenerator) target, value);
        } catch (Exception e) {
            throw new RuntimeException("Jackson JSON write failed", e);
        }
    }

    /** Stream-based read used by Hibernate when extracting JDBC JSON results. */
    @Override
    public <T> T readFromSource(JavaType<T> javaType,
                                Object source,
                                WrapperOptions options) {
        try {
            return mapper.readerFor(mapper.constructType(javaType.getJavaType()))
                    .readValue((JsonParser) source);
        } catch (Exception e) {
            throw new RuntimeException("Jackson JSON read failed", e);
        }
    }

    /** Serialize an object to a JSON string (used in some JDBC driver paths). */
    @Override
    public <T> String toString(T value, Type genericType) {
        try {
            final Type type = (genericType != null)
                    ? genericType
                    : (value != null ? value.getClass() : Object.class);
            return mapper.writerFor(mapper.constructType(type))
                    .writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Jackson stringify failed", e);
        }
    }

    /** Parse a JSON string into an object (used in some JDBC driver paths). */
    @Override
    public <T> T fromString(CharSequence json, Type genericType) {
        try {
            return mapper.readerFor(mapper.constructType(
                            genericType != null ? genericType : Object.class))
                    .readValue(json.toString());
        } catch (Exception e) {
            throw new RuntimeException("Jackson parse from String failed", e);
        }
    }

}