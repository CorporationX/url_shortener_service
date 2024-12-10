package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "hashes")
public class Hash {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
