package faang.school.urlshortenerservice.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;
}