package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash", indexes = {
        @Index(name = "idx_hash_value", columnList = "hash_value")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hash_seq_gen")
    @SequenceGenerator(
            name = "hash_seq_gen",
            sequenceName = "hash_id_seq",
            allocationSize = 1000
    )
    private Long id;

    @Column(name = "hash_value", nullable = false, unique = true, length = 10)
    private String hashValue;
}
