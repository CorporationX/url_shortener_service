package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hashes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Hash {

    @Id
    @Column(nullable = false, unique = true, length = 6)
    private String hash;
}
