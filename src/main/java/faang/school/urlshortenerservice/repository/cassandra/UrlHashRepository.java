package faang.school.urlshortenerservice.repository.cassandra;

import faang.school.urlshortenerservice.entity.UrlHash;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlHashRepository extends CassandraRepository<UrlHash, String> {
}