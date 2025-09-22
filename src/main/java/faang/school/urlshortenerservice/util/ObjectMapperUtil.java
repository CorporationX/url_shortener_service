package faang.school.urlshortenerservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public <T> T readValueAs(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize %s to %s".formatted(json, targetType), e);
        }
    }

    public String writeAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize %s".formatted(object), e);
        }
    }
}