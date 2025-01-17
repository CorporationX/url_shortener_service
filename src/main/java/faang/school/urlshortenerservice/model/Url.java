package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.redis.core.RedisHash;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "urls")
public class Url {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;

    @Column(name = "url",nullable = false, unique = true)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
