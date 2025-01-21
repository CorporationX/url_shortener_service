package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash", length = 7, nullable = false, unique = true)
    private String hash;
}
