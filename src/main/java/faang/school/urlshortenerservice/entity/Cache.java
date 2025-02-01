package faang.school.urlshortenerservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Cache")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cache {

    @Id
    private String hash;
    private String url;
    private LocalDateTime created_at;
}
