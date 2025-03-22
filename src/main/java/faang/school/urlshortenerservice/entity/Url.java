package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "hash", nullable = false)
    private String hash;

    @Column(name = "url")
    private String url;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
