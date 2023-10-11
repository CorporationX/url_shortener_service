package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<HashEntity, Long> {

    @Query(nativeQuery = true, value = """
                SELECT nextval('unique_hash_number_seq')
                FROM generate_series(1, :maxRange)
    """)
    List<Long> getNextRange(int maxRange);

    @Query(value = """
            SELECT nextval('unique_hash_number_seq')
            FROM generate_series(1, :limit)
    """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("limit") int limit);
}