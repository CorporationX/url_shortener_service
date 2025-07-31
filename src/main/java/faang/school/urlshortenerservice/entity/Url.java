package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;
    @Column(name = "url", nullable = false)
    private String url;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
