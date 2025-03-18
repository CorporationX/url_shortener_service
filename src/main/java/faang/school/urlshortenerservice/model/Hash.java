package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
@Getter
public class Hash {
    @Id
    @Column(length = 6, nullable = false)
    private String hash;

    @Column(name = "unique_number_seq", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unique_number_seq")
    @SequenceGenerator(name = "unique_number_seq_gen", sequenceName = "unique_number_seq", allocationSize = 1)
    private Long uniqueNumberSeq;
}
