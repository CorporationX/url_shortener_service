package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hash")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;
}
