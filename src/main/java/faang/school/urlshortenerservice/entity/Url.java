package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "hash", length = 7, nullable = false)
    private String hash;

    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Column(name = "last_get_at", nullable = false)
    private LocalDateTime lastGetAt;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;
}
