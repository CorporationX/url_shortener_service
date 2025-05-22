package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hash")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashEntity {

    @Id
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;
}
