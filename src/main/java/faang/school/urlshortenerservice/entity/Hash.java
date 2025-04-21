package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hash")
@Data
public class Hash {

    @Id
    @Column(name = "id", nullable = false)
    private String hash;
}
