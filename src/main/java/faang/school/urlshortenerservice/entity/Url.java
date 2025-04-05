package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at")
    private Timestamp created_at;
}
