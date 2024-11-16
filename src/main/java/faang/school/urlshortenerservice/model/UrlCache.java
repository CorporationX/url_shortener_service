package faang.school.urlshortenerservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Setter
@Getter
@RedisHash("url")
public class UrlCache implements Serializable {

    @Id
    private String hash;
    private String url;
}
