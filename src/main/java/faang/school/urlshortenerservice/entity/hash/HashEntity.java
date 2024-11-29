package faang.school.urlshortenerservice.entity.hash;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class HashEntity {

    @Id
    @Column(name = "hash")
    private String hash;
}
