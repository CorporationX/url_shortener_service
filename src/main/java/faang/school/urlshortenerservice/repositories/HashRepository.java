package faang.school.urlshortenerservice.repositories;

import faang.school.urlshortenerservice.entities.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long>, HashRepositoryCustom {
    @Query(nativeQuery = true, value = """
            select nextval('unique_hash_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getNextRange(long maxRange);

}
