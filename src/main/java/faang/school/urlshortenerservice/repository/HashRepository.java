package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<HashEntity, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO hash(hash)
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :limit)
            RETURNING hash
    """, nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("limit") int limit);
}
