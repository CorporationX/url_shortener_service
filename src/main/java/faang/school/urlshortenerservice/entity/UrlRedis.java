package faang.school.urlshortenerservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@Builder
@Accessors(chain = true)
@RedisHash("cacheUrl")
@ToString
public class UrlRedis {
    private String id;
    private String url;
}
