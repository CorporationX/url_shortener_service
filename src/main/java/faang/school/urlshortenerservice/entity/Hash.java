package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Entity(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    @NotNull
    @Length(min = 1, max = 6)
    private String hash;

    @Override
    public String toString() {
        return hash;
    }
}
