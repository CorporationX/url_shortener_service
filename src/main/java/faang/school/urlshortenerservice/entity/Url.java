package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;

    @NotNull
    @Size(min = 1, max = 4096)
    @Column(name = "url", nullable = false, length = 4096)
    private String url;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
