package faang.school.urlshortenerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RedisHash("UrlHash")
public class UrlRedis implements Serializable {

    @Id
    private String hash;

    private String url;
}
