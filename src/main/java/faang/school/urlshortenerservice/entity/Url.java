package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
    @SequenceGenerator(name = "url_seq", sequenceName = "url_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @Column(name = "url", length = 2048, nullable = false)
    private String url;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
