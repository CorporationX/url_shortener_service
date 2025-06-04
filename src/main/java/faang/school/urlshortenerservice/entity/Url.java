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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {

    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 6)
    private String hash;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
