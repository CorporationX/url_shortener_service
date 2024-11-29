package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "hash")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @Id
    @Column(name= "hash", unique = true, nullable = false, length = 6)
    private String hash;
}
