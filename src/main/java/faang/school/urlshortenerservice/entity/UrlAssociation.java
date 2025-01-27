package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UrlAssociation {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public UrlAssociation(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }
}
