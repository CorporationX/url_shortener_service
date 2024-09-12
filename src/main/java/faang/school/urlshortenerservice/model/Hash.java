package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "hash")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hash {
    @Id
    private String hash;
}
