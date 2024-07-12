package faang.school.urlshortenerservice.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "urls")
public class UrlHashRedis {

    @Id
    private String id;
    private String url;
    private LocalDateTime createdAt;
}
