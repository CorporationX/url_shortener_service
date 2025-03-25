package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url")
public class Url {
    @Id
    protected String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
