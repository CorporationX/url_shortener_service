package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long>{

    //Получение n уникальных значений из unique_number_seq в БД типа long
    @Query(nativeQuery = true, value = """
            SELECT nextval ('unique_number_seq') FROM generate_series(1,:n)
            """)
    List<Long> getUniqueNumbers(int n);

    //Сохранение списка хэшей (строки) в таблицу hash (батчом!)
    void save(List<Hash> hashes);

    //получает из таблицы hash n случайных хэшей и удаляет их оттуда
    // (можно сделать в postgres через слово returning). Это n хранится в конфиге, а не захардкожено.
    @Query(nativeQuery = true, value = """
            WITH rows_to_delete AS (
            SELECT id FROM hash
            LIMIT :amount
            )        
            DELETE FROM hash
            WHERE id IN (
            SELECT id FROM rows_to_delete)
             RETURNING *
            """)
    List<Hash> getHashBatch(long amount);
}
