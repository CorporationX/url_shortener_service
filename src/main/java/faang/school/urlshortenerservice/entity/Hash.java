package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "free_hash")
public class Hash {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "free_hash_id_seq")
    @SequenceGenerator(name = "free_hash_id_seq",
            sequenceName = "free_hash_id_seq",
            allocationSize =  100)
    private long id;

    @Column(name = "hash", length = 8, nullable = false, unique = true)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}