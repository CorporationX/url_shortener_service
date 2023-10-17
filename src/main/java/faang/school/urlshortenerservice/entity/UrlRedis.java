package faang.school.urlshortenerservice.entity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Builder
@Accessors(chain = true)
@RedisHash("cacheUrl")
@ToString
public class UrlRedis implements Serializable {
    @Id
    private String id;
    private String url;
}
