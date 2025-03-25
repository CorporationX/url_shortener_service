package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 8)
    private String hash;

    @Column(name = "url", nullable = false, unique = true, length = 16384)
    private String url;

    @Column(name = "expired_at")
    private LocalDateTime expiredAtDate;
}
