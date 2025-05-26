package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "url")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}