package faang.school.urlshortenerservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "hash")
@Data
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //TODO проверить не упадет ли если длина хэша будет меньше 7, если что подсчитать минимум с которого
    // должен генерироваться sequence в БД (поменять скрипт)
    @Column(name = "hash", unique = true, nullable = false, length = 7)
    private String hash;

    public Hash(String hash) {
        this.hash = hash;
    }
}
