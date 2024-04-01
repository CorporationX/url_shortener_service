package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;
}
