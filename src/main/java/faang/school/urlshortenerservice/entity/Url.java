package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "urls")
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    @Id
    @Column(name = "hash")
    private String hashString;
    @Column(name = "url")
    private String originalUrl;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
