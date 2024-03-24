package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alexander Bulgakov
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "hashes")
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "hash", unique = true, length = 7, nullable = false)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
