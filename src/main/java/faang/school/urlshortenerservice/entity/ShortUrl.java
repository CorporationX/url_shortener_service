package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "short_url")
public class ShortUrl {

    @Id
    @Column(name = "hash")
    private String hash;

    @Column(name = "actual_url", nullable = false, unique = true)
    private String actualUrl;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}