package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Data
public class Url {
    @Id
    @SequenceGenerator(name = "sequence_id_auto_gen", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id_auto_gen")
    private long id;
    @Column(nullable = false)
    private String hash;
    @Column(nullable = false)
    private URL url;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
