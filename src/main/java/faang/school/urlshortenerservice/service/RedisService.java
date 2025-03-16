package faang.school.urlshortenerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    public void setValue(String key, String value) {
        try (Jedis connection = jedisPool.getResource()) {
            connection.set(key, value);
            log.info("Created new key-value pair in cache: key: {}, value: {}", key, value);
        }
    }

    public String getValue(String key) {
        try (Jedis connection = jedisPool.getResource()) {
            String json = connection.get(key);
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.get("url").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
