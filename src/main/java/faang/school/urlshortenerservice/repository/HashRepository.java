package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getNextRange(@Param("maxRange") int maxRange);
}
