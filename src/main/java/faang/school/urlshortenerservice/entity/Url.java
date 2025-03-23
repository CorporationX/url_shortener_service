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

@Getter
@Setter
@Builder
@Entity
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @Column(name = "url", length = 1024, nullable = false)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
