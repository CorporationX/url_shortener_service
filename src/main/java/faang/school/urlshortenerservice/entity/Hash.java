package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Hash {
    @Id
    @Column(name = "hash", nullable = false, unique = true, length = 6)
    private String hash;
}
