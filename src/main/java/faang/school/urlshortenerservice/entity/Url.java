package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.mapper.URLToStringMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url")
@Entity
public class Url {

    @Id
    @Column(name = "hash", updatable = false, nullable = false)
    private String hash;

    @Convert(converter = URLToStringMapper.class)
    @Column(name = "url", nullable = false)
    private URL url;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delete_at", nullable = false)
    private LocalDateTime deletedAt;
}
