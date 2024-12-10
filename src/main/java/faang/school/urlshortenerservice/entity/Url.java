package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url implements Serializable {

    @Id
    @Column(name = "hash", length = 6, unique = true, nullable = false)
    private String hash;

    @Column(name = "url", unique = true, nullable = false, length = 4096)
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
