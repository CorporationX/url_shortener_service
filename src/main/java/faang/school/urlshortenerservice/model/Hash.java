package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "free_hash_set")
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Hash {
    @Id
    @Column(name = "hash_value")
    private String hash;
}