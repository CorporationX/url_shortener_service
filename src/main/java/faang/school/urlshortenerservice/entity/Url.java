package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {
    @Id
    @Column(length = 6)
    private String hash;

    @Column(nullable = false)
    private String url;

    @Column(name = "last_get_at", nullable = false)
    private LocalDateTime lastGetAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}