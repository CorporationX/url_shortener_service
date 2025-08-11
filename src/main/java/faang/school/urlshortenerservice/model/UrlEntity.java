package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlEntity {

    @Id
    private String hash;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UrlEntity(String hash, String originalUrl) {
        this.hash = hash;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
    }
}
