package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Url {
    @Id
    @Column(name = "hash", nullable = false, length = 8)
    private String hash;

    @Column(name = "url", nullable = false, length = 1024)
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
