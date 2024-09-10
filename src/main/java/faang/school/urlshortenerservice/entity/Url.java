package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "urls")
public class Url {

    @Column(name = "hash", length = 8, nullable = false, unique = true)
    private String hash;

    @Column(name = "url", length = 1024, nullable = false, unique = true)
    private String originalUrl;
}
