package faang.school.urlshortenerservice.model.hash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash")
@Entity
public class Hash {
    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;
}
