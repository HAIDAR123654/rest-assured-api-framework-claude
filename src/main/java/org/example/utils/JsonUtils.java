package org.example.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class JsonUtils {

    private static final ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(() ->
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    );

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return mapper.get().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }

    public static <T> T deserializeFromFile(String filePath, Class<T> clazz) {
        try {
            return mapper.get().readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from file", e);
        }
    }

    public static String serialize(Object obj) {
        try {
            return mapper.get().writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public static void removeThreadLocalMapper() {
        mapper.remove();
    }
}
