package faang.school.urlshortenerservice.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hash")
@Data
public class Hash {
    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}