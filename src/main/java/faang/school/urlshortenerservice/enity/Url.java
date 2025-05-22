package faang.school.urlshortenerservice.enity;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Url {

    @Id
    @Column(name = "hash", length = 8, unique = true, nullable = false)
    private String hash;

    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Column(name = "last_get_at", nullable = false)
    private LocalDateTime lastGetAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;
}
