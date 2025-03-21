package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash")
public class Hash {

    @Id
    @SequenceGenerator(name = "hash_seq", sequenceName = "unique_hash_number_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hash_seq")
    private Long id;

    @Column(name = "hash",nullable = false, unique = true)
    private String hash;
}
