package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "url")
@Data
public class Url {
    @Id
    @Column(nullable = false, unique = true, length = 7)
    private long hash;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "created_at", nullable = false)
    private String createdAt;
}
