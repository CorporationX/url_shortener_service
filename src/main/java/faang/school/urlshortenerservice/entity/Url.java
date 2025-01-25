package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;

    @Column(name = "url", nullable = false, length = 1024)
    private String url;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
