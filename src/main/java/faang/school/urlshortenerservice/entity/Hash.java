package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hash {

    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @OneToOne
    @JoinColumn(name = "hash", referencedColumnName = "hash")
    private Url url;
}
