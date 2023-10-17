package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "url")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash", nullable = false, unique = true, length = 6)
    private String hash;

    @Column(name = "url", nullable = false, length = 100)
    private String url;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private ZonedDateTime createdAt;
}