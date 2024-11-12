package faang.school.urlshortenerservice.model.hash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", nullable = false, length = 6)
    @Size(min = 1, max = 6)
    private String hash;

    @Override
    public String toString() {
        return "HashEntity{"
                + "hash='" + hash + '\''
                + '}';
    }
}
