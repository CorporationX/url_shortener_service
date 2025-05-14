package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "url", indexes = {
        @Index(name = "idx_url_url", columnList = "url")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_url_url", columnNames = {"url"})
})
@Getter
@Setter
public class UrlEntity {

    @Id
    private String hash;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
