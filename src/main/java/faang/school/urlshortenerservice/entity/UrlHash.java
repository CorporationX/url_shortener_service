package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UrlHash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "hash", nullable = false, length = 8)
    private String hash;
}
