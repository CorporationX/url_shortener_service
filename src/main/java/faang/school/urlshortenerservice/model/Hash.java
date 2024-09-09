package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    private String hash;
}
