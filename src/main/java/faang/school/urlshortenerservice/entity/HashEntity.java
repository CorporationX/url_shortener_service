package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "hash")
@Data
public class HashEntity {

    @Id
    @Column(name = "hash", nullable = false)
    private String hash;

    public HashEntity(String hash) {
        this.hash = hash;
    }
}
