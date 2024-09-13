package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BaseEntity {
    @Id
    private Long id;
}
