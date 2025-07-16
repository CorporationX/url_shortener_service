package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hash")
public class Hash {
    @Id
    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;
}
