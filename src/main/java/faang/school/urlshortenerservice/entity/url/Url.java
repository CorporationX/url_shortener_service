package faang.school.urlshortenerservice.entity.url;

import faang.school.urlshortenerservice.entity.hash.Hash;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "url")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", length = 2048, nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToOne
    @JoinColumn(name = "hash", referencedColumnName = "hash")
    private Hash hash;
}
