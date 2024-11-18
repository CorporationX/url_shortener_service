package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "url", timeToLive = 10L)
public class Url {

    @org.springframework.data.annotation.Id
    @Id
    private String hash;

    private String url;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Url(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }
}
