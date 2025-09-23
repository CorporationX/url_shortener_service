package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Hash — класс сущность для хэша.
 *
 * @author bozya
 * @since 12.09.2025
 */
@Entity
@Getter
@Setter
@Table(name = "hashes")
@NoArgsConstructor
@AllArgsConstructor
public class HashEntity {

    @Id
    @Column(name = "hash", length = 7)
    private String hash;
}