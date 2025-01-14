package faang.school.urlshortenerservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "url")
public class Url {
    @Id
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
