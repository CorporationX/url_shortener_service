package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "hash")
    private String hash;

    @Indexed
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}