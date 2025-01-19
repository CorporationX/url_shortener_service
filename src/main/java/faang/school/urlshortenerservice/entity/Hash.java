package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Hash {
    @Id
    @Column(name = "hash", nullable = false, length = 8)
    private String hash;
}
