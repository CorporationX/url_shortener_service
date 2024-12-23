package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash")
public class Hash {

    @Id
    @Column(name = "hash", length = 6)
    private String hash;

}
