package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link Url} в базе данных.
 * Предоставляет методы для поиска, удаления и управления URL.
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Находит URL по его хэшу.
     *
     * @param hash Хэш короткого URL.
     * @return {@link Optional}, содержащий URL, если он найден, иначе пустой {@link Optional}.
     */
    Optional<Url> findUrlByHash(String hash);

    /**
     * Находит URL по полному URL.
     *
     * @param url Полный URL.
     * @return {@link Optional}, содержащий URL, если он найден, иначе пустой {@link Optional}.
     */
    Optional<Url> findByUrl(String url);

    /**
     * Удаляет URL, созданные более суток назад, и возвращает их хэши.
     * Использует нативный SQL-запрос для удаления записей.
     *
     * @return Список хэшей удалённых URL.
     */
    @Modifying
    @Query(nativeQuery = true, value = """
           DELETE FROM url WHERE url.created_at < now() - INTERVAL '1 day' RETURNING url.hash
        """)
    List<String> removeUrlOlderThanOneDay();
}
