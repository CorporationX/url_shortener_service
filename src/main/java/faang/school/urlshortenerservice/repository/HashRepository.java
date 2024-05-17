package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true,
            value = "SELECT nextval(unique_number_sequence) FROM generate_series(1, batchSize) ")
    List<Long> getFollowingRangeUniqueNumbers(@Param("n") int batchSize);

    // Метод для сохранения хэшей в БД
    //@Modifying
    void saveAll(List<String> hashes);
        // Здесь должен быть SQL-запрос для вставки хэшей в таблицу hash
        // Например, если вы используете PostgreSQL, запрос может выглядеть так:
        // INSERT INTO hash (hash) VALUES (?) hashRepository.saveAll(hashes);
}