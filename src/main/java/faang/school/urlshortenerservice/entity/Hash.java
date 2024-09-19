package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hash")
@NoArgsConstructor
@Getter
@Setter
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
