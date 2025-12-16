package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("url")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CachedUrl {
    @Id
    private String id;
    private String url;
}
