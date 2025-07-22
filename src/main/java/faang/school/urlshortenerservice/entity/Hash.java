package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "hash")
public class Hash {
    @Id
    @Column(name = "hash", length = 7)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
