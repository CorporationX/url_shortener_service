package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@NoArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;
}
