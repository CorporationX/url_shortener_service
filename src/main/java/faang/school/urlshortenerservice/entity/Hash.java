package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "hash")
@RequiredArgsConstructor
public class Hash {
    @Id
    @Column(nullable = false, unique = true, length = 7)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
