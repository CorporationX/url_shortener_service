package faang.school.urlshortenerservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash("Url")
public class UrlRedis {
    @Id 
    private String hash;
    private String url;
    private LocalDateTime createdAt;
}
