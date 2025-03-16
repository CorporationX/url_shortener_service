package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hash")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash")
    private String hash;
}
