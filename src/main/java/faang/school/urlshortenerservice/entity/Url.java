package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Url {

    @Id
    @Column(name = "hash", length = 6)
    private String hash;

    @NotBlank
    @Column(name = "url", length = 2048, nullable = false)
    private String originalUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
