package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @Column(length = 6, nullable = false)
    private String hash;

    @Column(length = 2048, nullable = false)
    private String url;

    @CreationTimestamp
    private Timestamp createdAt;
}
