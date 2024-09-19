package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;
}
