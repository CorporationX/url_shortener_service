package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * UrlEntity — сущность для url ссылок.
 *
 * @author bozya
 * @since 19.09.2025
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url")
public class UrlEntity {

    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 7)
    private String hash;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}