package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hash")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hash_seq")
    @SequenceGenerator(
            name = "hash_seq",
            sequenceName = "hash_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
