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
            SELECT nextval ('unique_number_seq') FROM generate_series(1,1000)
            """)
    List<Long> getUniqueNumbers();

    //Сохранение списка хэшей (строки) в таблицу hash (батчом!)

    void save(List<Hash> hashes);
}
