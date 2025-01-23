package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Репозиторий для работы с сущностями {@link Hash} в базе данных.
 * Предоставляет методы для получения уникальных чисел и удаления хэшей.
 */
public interface HashRepository extends JpaRepository<Hash, Long> {

    /**
     * Получает список уникальных чисел из последовательности.
     * Использует нативный SQL-запрос для генерации чисел.
     *
     * @param maxRange Количество уникальных чисел, которые нужно получить.
     * @return Список уникальных чисел.
     */
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    /**
     * Удаляет указанное количество хэшей из таблицы и возвращает их.
     * Использует нативный SQL-запрос для удаления и возврата записей.
     *
     * @param amount Количество хэшей, которые нужно удалить.
     * @return Список удалённых хэшей.
     */
    @Query(nativeQuery = true, value = """
                DELETE FROM hash AS h WHERE hash IN (SELECT hash FROM hash AS h LIMIT :amount)
                RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}
