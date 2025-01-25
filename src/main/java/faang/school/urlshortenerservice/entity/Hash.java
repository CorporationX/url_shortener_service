package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;
}
