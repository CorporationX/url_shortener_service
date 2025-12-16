package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "url")
@Setter
@Getter
@NoArgsConstructor
public class Url {

    @Id
    private String hash;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Url(String hash, String originalUrl) {
        this.hash = hash;
        this.originalUrl = originalUrl;
    }
}
