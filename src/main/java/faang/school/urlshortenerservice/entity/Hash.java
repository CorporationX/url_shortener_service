package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    @SequenceGenerator(name = "sequence_id_auto_gen", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id_auto_gen")
    private long id;
    @Column(nullable = false)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
