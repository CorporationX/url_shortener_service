package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "long_url")
    private String longUrl;
}
