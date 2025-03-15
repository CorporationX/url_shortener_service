package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = "select nextval('unique_number_seq') from generate_series(1, 1000)")
    List<Long> getUniqueNumbers();

    void getHashBatch();
}

//Метод save(hashes) сохраняет список хэшей батчом (или батчами), а не каждый хэш
//        отдельным запросом, в таблицу hash.
//
//Метод getHashBatch() — получает из таблицы hash n случайных хэшей и удаляет их оттуда
//(можно сделать в postgres через слово returning). Это n хранится в конфиге, а не захардкожено.