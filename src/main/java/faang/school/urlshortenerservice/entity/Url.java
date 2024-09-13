package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "url")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash")
    private String hash;

    @Column(name = "url")
    private String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private LocalDateTime create_at;
}
