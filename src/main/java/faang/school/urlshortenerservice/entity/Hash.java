package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Hash {

    @Id
    @Column(name = "hash_value", nullable = false)
    private String hashValue;

}
