package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url", indexes = {
        @Index(name = "idx_url_hash", columnList = "hash")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class URL {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_id_seq_gen")
    @SequenceGenerator(
            name = "url_id_seq_gen",
            sequenceName = "url_id_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(name = "hash", nullable = false, unique = true, length = 10)
    private String hash;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "access_count", nullable = false)
    private Long accessCount = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
