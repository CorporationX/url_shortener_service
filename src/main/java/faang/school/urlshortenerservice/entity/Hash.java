package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hash")
@Getter
@Setter
@NoArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 6)
    private String hash;

    @Builder
    public Hash(String hash) {
        this.hash = hash;
    }
}
