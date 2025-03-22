package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
@Getter
public class Hash {
    @Id
    @Column(length = 6, nullable = false)
    private String hash;
}
