package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@RedisHash("Url")
public class RedisCashUrl {
    @Id
    String hash;
    UrlDto urlDto;
}
