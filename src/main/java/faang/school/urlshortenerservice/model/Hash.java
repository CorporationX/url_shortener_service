package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hash")
@Data
public class Hash {
    @Id
    private String hash;
}
