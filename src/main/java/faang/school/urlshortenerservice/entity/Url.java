package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Url {
    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime created_at;
}
