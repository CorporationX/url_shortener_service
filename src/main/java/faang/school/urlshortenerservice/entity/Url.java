package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;
    @Column(name = "url", length = 2000, nullable = false)
    private String url;
    @Column(name = "created_at")
    private LocalDateTime expirationTime;
}
