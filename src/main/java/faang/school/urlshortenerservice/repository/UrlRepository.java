package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с URL в базе данных.
 * Предоставляет методы для поиска, проверки и удаления сокращенных ссылок.
 *
 * @author bozya
 * @since 19.09.2025
 */
@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, String> {

    /**
     * Находит URL по оригинальному URL.
     *
     * @param url оригинальный URL для поиска
     * @return сущность URL если найдена
     */
    Optional<UrlEntity> findByUrl(String url);

    /**
     * Находит оригинальный URL по хэшу.
     *
     * @param hash хэш короткой ссылки
     * @return оригинальный URL если найден
     */
    @Query(nativeQuery = true, value = """
                SELECT url
                FROM url
                WHERE hash = :hash
                """)
    Optional<String> findUrlByHash(@Param("hash") String hash);

    /**
     * Удаляет устаревшие URL и возвращает их хэши для повторного использования.
     *
     * @param retentionPeriod период устаревания в формате SQL INTERVAL
     * @return список освободившихся хэшей
     */
    @Modifying
    @Query(nativeQuery = true, value = ("""
                DELETE
                FROM url
                WHERE created_at < NOW() - INTERVAL :retentionPeriod
                RETURNING hash
                """))
    List<String> deleteOldUrlsAndReturnHash(@Param("retentionPeriod") String retentionPeriod);

    /**
     * Проверяет существование хэша в базе данных.
     *
     * @param hash хэш для проверки
     * @return true если хэш существует
     */
    boolean existsByHash(String hash);
}