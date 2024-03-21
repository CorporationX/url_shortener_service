package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash("UrlCache")
public class UrlCache {
    @Id
    private String hash;
    private String url;
}
