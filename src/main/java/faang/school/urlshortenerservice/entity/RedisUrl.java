package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Url")
public class RedisUrl {
    @Id
    private Long id;
    @Getter
    private String hash;
    private String url;
}
