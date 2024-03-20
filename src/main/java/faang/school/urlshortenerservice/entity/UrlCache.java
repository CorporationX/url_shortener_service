package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@RedisHash("UrlCache")
public class UrlCache {
    @Id
    private String url;
    private String hash;
}
