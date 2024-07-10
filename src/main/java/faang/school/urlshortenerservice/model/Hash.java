package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    private String hash;

    @Override
    public String toString() {
        return hash;
    }
}