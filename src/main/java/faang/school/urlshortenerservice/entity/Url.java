package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "url")
public class Url {
    @Id
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
