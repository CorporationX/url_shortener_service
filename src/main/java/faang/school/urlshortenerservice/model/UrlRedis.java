package faang.school.urlshortenerservice.model;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@RedisHash("url")
public class UrlRedis implements Serializable {
    @Id
    private String hash;
    private String url;
}