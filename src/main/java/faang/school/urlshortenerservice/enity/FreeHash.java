package faang.school.urlshortenerservice.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "free_hash_id_seq")
    @SequenceGenerator(name = "free_hash_id_seq",
            sequenceName = "free_hash_id_seq",
            allocationSize =  1000)
    private long id;

    @Column(name = "hash", length = 8, nullable = false, unique = true)
    private String hash;

    public FreeHash(String hash) {
        this.hash = hash;
    }
}
