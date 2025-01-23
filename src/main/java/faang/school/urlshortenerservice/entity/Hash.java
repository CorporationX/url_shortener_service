package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность, представляющая хэш в базе данных.
 * Хэш используется для создания коротких URL.
 */
@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Hash {

    /**
     * Уникальный идентификатор хэша.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный хэш, представляющий собой короткий URL.
     */
    @Column(name = "hash", nullable = false, unique = true)
    private String hash;

    /**
     * Создаёт объект хэша с указанным значением.
     *
     * @param hash Значение хэша.
     */
    public Hash(String hash) {
        this.hash = hash;
    }
}
