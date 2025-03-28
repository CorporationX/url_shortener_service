package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "urls")
@Entity
public class Url {

    @Id
    @Column(name = "hash")
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiredAt == null) {
            expiredAt = createdAt.plusMonths(1);
        }
    }
}
