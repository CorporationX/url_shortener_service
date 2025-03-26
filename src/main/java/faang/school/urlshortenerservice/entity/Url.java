package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "url")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Url {
    @Id
    private String hash;

    @Column(name = "url", length = 2000, nullable = false)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public Url(String hash, String url, LocalDateTime expiresAt) {
        this.hash = hash;
        this.url = url;
        this.expiresAt = expiresAt;
    }
}