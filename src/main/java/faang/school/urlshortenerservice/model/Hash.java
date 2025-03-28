package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Hash {

    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;
}
