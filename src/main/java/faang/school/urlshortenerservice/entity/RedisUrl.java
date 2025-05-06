package faang.school.urlshortenerservice.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RequiredArgsConstructor
@RedisHash("RedisUrl")
public class RedisUrl {
    @Id
    String hash;
    String url;
}
