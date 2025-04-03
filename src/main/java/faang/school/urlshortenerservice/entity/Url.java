package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "url")
@Data
@Builder
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

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;


    public Url(String hash, String url, LocalDateTime deletedAt) {
        this.hash = hash;
        this.url = url;
        this.deletedAt = deletedAt;
    }
}

