package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hash")
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unique_number_seq")
    @SequenceGenerator(name = "unique_number_seq", sequenceName = "unique_number_sequence", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;
}