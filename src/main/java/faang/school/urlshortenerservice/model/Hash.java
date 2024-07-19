package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hash")
@NoArgsConstructor
public class Hash {

    @Id
    @Column(nullable = false)
    private String hash;


    public Hash(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Hash cannot be null");
        }
        this.hash = hash;
    }
}
