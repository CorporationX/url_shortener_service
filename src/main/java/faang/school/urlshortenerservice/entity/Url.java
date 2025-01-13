package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Url {
    @Id
    @Column(nullable = false)
    private String hash;
    @Column(nullable = false)
    private String url;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
