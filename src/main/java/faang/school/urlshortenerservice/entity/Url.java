package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_sequence")
    @SequenceGenerator(name = "url_sequence", sequenceName = "unique_number_seq", allocationSize = 1)
    private Long id;


    @Column
    private long hashId;

    @ManyToOne
    @JoinColumn(name = "hash_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Hash hash;

    @Column
    private String url;

    @Column
    @CreationTimestamp
    private LocalDateTime created_at;
}
