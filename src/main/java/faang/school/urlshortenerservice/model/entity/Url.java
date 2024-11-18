package faang.school.urlshortenerservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "url")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "long_url", nullable = false, unique = true)
    private String longUrl;

    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
