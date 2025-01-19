package faang.school.urlshortenerservice.DTO;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hash {


    @Id
    private String hashValue;
    private Long uniqueNumber;
}
