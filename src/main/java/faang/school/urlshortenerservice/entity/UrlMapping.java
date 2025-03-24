package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.enums.HashStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "url_mappings")
public class UrlMapping {
    @Id
    private String hash;

    @Column(name = "long_url", nullable = false)
    private String longUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HashStatus status = HashStatus.ACTIVE;

    public UrlMapping(String hash, String longUrl, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.hash = hash;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}
