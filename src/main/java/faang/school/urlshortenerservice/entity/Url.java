package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    @Id
    @Column(name = "hash", nullable = false)
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

