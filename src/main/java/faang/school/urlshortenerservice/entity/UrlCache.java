package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UrlCache")
@Builder
@Getter
@Setter
public class UrlCache {

    @Id
    private String hash;

    private String url;
}
