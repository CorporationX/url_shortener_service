package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    // Метод для получения уникальных чисел из БД
    private List<Long> getUniqueNumbers(int count) {
        // Здесь должен быть SQL-запрос для получения уникальных чисел
        // Например, если вы используете PostgreSQL, запрос может выглядеть так:
        // SELECT generate_series(1, count) AS number FROM generate_series(1, count) AS series;
        return null; // Заглушка, необходимо реализовать получение чисел из БД
    }

    // Метод для сохранения хэшей в БД
    private void saveAll(List<String> hashes) {
        // Здесь должен быть SQL-запрос для вставки хэшей в таблицу hash
        // Например, если вы используете PostgreSQL, запрос может выглядеть так:
        // INSERT INTO hash (hash) VALUES (?)
        // hashRepository.saveAll(hashes);
    }
}