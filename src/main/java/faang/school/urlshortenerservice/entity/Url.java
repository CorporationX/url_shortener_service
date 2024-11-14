package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "hash")
    @Length(max = 6)
    private String hash;

    @Column(name = "hash")
    @Length(max = 128)
    private String url;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
