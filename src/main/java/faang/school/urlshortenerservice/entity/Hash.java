package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hash")
public class Hash {

    @Id
    private Long id;
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;
}
