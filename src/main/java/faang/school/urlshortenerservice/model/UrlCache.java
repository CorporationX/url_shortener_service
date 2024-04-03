package faang.school.urlshortenerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "urlCash", timeToLive = 86400L)
public class UrlCache {
    @Id
    private String hash;
    @Indexed
    private String url;
}