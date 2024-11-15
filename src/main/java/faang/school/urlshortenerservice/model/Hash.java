package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash")
public class Hash {

    @Id
    @Length(max = 6)
    private String hash;

}
