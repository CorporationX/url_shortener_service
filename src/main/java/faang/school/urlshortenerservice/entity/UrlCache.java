package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@RedisHash(value = "UrlCache", timeToLive = 86400)
public class UrlCache implements Serializable {
    @Id
    private String hash;
    @Indexed
    private String url;
}
