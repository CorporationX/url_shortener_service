package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "hash")
public class Hash {
    @Id
    @Column(name = "hash", length = 8, nullable = false, unique = true)
    private String hash;
}
