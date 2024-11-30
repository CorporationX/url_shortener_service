package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hash")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", length = 6, nullable = false, unique = true)
    private String hash;
}
