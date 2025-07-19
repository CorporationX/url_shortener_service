package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "hash")
@AllArgsConstructor
public class Hash {

    @Id
    @Column(length = 6, nullable = false)
    private String hash;
}
