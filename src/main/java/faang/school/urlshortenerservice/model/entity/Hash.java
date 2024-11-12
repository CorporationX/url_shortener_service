package faang.school.urlshortenerservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hash")
public class Hash {
    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;
}
