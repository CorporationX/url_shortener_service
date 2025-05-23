package faang.school.urlshortenerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("UrlHash")
public class UrlRedis implements Serializable {

    @Id
    private String hash;

    private String url;

    @TimeToLive
    private Long timeToLive;
}
