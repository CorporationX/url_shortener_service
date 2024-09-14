package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
public class Hash extends BaseEntity {
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;
}
