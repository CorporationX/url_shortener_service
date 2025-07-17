package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hashes")
public class Hash {
    @Id
    @Column(name = "hash",
    length = 6, nullable = false, updatable = false)
    private String hash;
}