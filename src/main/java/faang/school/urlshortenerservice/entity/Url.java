package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "url")
public class Url {

    @Id
    @Size(max = 6, min = 5)
    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Url(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }
}
