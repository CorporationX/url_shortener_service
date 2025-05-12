package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "url")
@Data
public class Url {

    @Id
    @Column(length = 6)
    private String hash;

    @Column(nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}
