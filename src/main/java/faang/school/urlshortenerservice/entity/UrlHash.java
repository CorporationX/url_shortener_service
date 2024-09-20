package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UrlHash {
    @Id
    @GeneratedValue(generator = "hash_id_generator")
    @GenericGenerator(name = "hash_id_generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "hash_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1")
            })
    private long id;

    @Column(name = "hash", nullable = false, length = 8)
    private String hash;
}
