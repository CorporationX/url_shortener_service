package faang.school.urlshortenerservice.model.entity;

import faang.school.urlshortenerservice.model.enums.UrlStatus;
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

import static faang.school.urlshortenerservice.model.enums.UrlStatus.CHECKING;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "url")
@Entity
public class Url {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @Column(name = "url", length = 2000, nullable = false)
    private String url;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UrlStatus status = CHECKING;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
}