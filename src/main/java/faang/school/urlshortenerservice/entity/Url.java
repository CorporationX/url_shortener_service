package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(force = true)
@Entity
@Table(name = "url")
public class Url {

    @Id
    @Column(length = 6, nullable = false)
    private final String hash;

    @Column(nullable = false)
    private final String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public Url(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }
}