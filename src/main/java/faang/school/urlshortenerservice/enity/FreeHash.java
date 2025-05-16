package faang.school.urlshortenerservice.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @Column(name = "hash", length = 8, nullable = false, unique = true)
    private String hash;
}
