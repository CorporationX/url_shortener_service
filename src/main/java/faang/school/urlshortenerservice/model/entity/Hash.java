package faang.school.urlshortenerservice.model.entity;

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

@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Hash {

    public Hash(String hash) {
        this.hash = hash;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hash_seq_gen")
    @SequenceGenerator(
            name = "hash_seq_gen",
            sequenceName = "hash_seq",
            allocationSize = 100
    )
    private long id;

    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;
}
