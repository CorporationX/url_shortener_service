package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
@RedisHash(value = "UrlCache",timeToLive = 86400)
public class UrlCache {
    @Id
    private String hash;
    @Indexed
    private String url;
}
