package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@ToString
@RedisHash("Url")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url {

    @Id
    @Column(length = 6, nullable = false)
    private String hash;

    @Column(nullable = false)
    private String url;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
