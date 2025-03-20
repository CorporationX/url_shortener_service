package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Url {

    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
