package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @NotNull
    private String hash;

    @Override
    public String toString() {
        return hash;
    }
}