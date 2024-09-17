package faang.school.urlshortenerservice.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "UrlInRedis")
@Data
public class UrlInRedis implements Serializable {
    private String id;
    private String longUrl;
    private LocalDateTime createdAt;
}
