package dev.gabriel.consumer_worker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * utility component for json serialization and deserialization using jackson.
 * <p>
 * this class provides safe wrapper methods to handle json operations without
 * requiring checked exceptions in the business logic layers.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * safely converts an object to its json string representation.
     * <p>
     * if the conversion fails, it logs the error and returns the object's
     * {@code toString()} value as a fallback, ensuring the application doesn't crash.
     * </p>
     *
     * @param obj the object to be serialized.
     * @return the json string or the tostring() representation in case of error.
     */
    public String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON conversion error", e);
            return String.valueOf(obj); // fallback to tostring()
        }
    }

    /**
     * converts a json string to a typed java object.
     *
     * @param json  the json string.
     * @param clazz the target class type.
     * @param <T>   the type of the object.
     * @return the deserialized object.
     * @throws RuntimeException if deserialization fails.
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing JSON to class {}", clazz.getSimpleName(), e);
            throw new RuntimeException("JSON Deserialization failed", e);
        }
    }
}