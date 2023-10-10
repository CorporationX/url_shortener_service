package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<HashEntity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(int maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *
            """)
    List<HashEntity> getHashBatch(int batchSize);
}
