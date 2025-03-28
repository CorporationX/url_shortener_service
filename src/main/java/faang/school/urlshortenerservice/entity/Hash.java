package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "hash")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;

    @Override
    public String toString() {
        return hash;
    }
}
