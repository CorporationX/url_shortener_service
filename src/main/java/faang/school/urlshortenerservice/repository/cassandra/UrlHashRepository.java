package faang.school.urlshortenerservice.repository.cassandra;

import faang.school.urlshortenerservice.entity.UrlHash;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlHashRepository extends CassandraRepository<UrlHash, String> {

    @Query("SELECT full_url FROM urls WHERE hash = ?0")
    String findByHash(String hash);
}