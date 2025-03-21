package faang.school.urlshortenerservice.model;

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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "hash", nullable = false,  unique = true, length = 8)
    String hash;

    //@URL
    @Column(name = "url", nullable = false, unique = true, length = 16384)
    String url;

    @Column(name = "expired_at")
    LocalDateTime expiredAtDate;
}
