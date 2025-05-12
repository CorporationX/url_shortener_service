package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UniqueNumberSeq {

    @Id
    @GeneratedValue(generator = "unique_number_seq_generator")
    @SequenceGenerator(name = "unique_number_seq_generator", sequenceName = "unique_number_seq", allocationSize = 1)
    private Long id;
}
