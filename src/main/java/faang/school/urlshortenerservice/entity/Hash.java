package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hashes")
@SequenceGenerator(
        name = "unique_number_sequence",
        sequenceName = "unique_number_seq",
        allocationSize = 1
)
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unique_number_seq")
    @Column(name = "hash",
    length = 6, nullable = false, updatable = false)
    private String hash;
}
//43:51