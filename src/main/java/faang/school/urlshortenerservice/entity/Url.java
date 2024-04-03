package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    private String url;

    private Timestamp created_at;
}