package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Table(name = "hash")
@Entity
@NoArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash")
    private String hash;
}
