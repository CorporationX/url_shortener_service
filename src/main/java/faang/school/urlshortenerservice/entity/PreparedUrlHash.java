package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "prepared_hashes")
public class PreparedUrlHash {

    @Id
    @Column(name = "hash", length = 6, unique = true, nullable = false)
    private String hash;

    @Column(name = "taken", nullable = false)
    private boolean taken;
}