package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash")
public class Hash {
    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 6)
    private String hash;
}
