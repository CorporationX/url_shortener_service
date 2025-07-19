package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <h2>Задание</h2>
 * <div>Создать класс repository, который будет иметь два метода:</div>
 * <li>Получение n уникальных значений из unique_number_seq в БД типа long;</li>
 * <li>Сохранение списка хэшей (строки) в таблицу hash (батчом!).</li>
 * <h2>Критерии приема</h2>
 * <li>HashRepository — спринг бин с соответствующей аннотацией.</li>
 * <li>Метод getUniqueNumbers(n) действительно возвращает n уникальных чисел из sequence unique_numbers_seq в БД.</li>
 * <li>Метод save(hashes) сохраняет список хэшей батчом (или батчами), а не каждый хэш отдельным запросом,
 * в таблицу hash.</li>
 * <li>Метод getHashBatch() — получает из таблицы hash n случайных хэшей и удаляет их оттуда (можно сделать в
 * postgres через слово returning). Это n хранится в конфиге, а не захардкожено.</li>
 * <li>Можно использовать Spring Data, если удобно. Если не очень, то подойдет JdbcTemplate.</li>
 * <li>Не нужно мапить эти данные на какие-либо hibernate сущности.</li>
 * <li>Везде используются lombok аннотации.</li>
 */
public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
        select nextval('unique_number_seq') FROM generate_series(1, :range)
        """)
    List<Long> getUniqueNumbers(@Param("range") Long range);

    @Modifying
    @Query(nativeQuery = true, value = """
        delete from hashes
         where hash in (
                 select dd.hash from hashes dd limit :range
               )
        returning *
        """)
    List<String> getPortionOfHashes(@Param("range") Long range);

    @Query(nativeQuery = true, value = """
        select count(*) from public.hashes
        """)
    Long countAll();
}