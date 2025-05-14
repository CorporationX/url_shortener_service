package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sequence {
    @Id
    @GeneratedValue(generator = "unique_seq_number_gen")
    @SequenceGenerator(name = "unique_seq_number_gen",
            sequenceName = "unique_seq_number",
            allocationSize = 1)
    private Long id;
}
