package faang.school.urlshortenerservice.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "free_hash")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FreeHash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "hash")
    private String hash;

    public FreeHash(String hash) {
        this.hash = hash;
    }
}
