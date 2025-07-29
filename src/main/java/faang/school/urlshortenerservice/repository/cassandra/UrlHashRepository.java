package faang.school.urlshortenerservice.repository.cassandra;

import faang.school.urlshortenerservice.entity.UrlHash;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlHashRepository extends CassandraRepository<UrlHash, String> {

    @Query("SELECT full_url FROM urls WHERE hash = ?0")
    Optional<String> findByHash(String hash);

    @Query("UPDATE urls SET full_url = null WHERE hash = ?0")
    void freeReusedEntityByHash(String hash);

    // Добавить запрос для добавления который выставляет дату?
}