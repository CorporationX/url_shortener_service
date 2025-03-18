package faang.school.urlshortenerservice.model;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Getter
public class Url {
    @Id
    @Column(length = 6, nullable = false)
    private String hash;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "hash", referencedColumnName = "hash", insertable = false, updatable = false)
    private Hash hashReference;

}
