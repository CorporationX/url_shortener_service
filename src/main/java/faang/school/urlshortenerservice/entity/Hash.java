package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "hashes")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
    @SequenceGenerator(name = "url_seq", sequenceName = "url_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "hash", length = 6, unique = true)
    private String hash;

    @ManyToOne
    @JoinColumn(name = "url_id")
    private Url url;
}
