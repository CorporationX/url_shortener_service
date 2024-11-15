package faang.school.urlshortenerservice.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "RedisCachedUrl", timeToLive = 3600L)
@Getter
@Setter
public class RedisCachedUrl implements Serializable {

    private String id;
    private String longUrl;
    private LocalDateTime createdAt;
}
