package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "hash")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @Column(length = 6, nullable = false)
    private String hash;

    @Column(name = "url", length = 2048, nullable = false)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
