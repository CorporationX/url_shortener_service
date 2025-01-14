package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@NoArgsConstructor
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }

}
