package faang.school.urlshortenerservice.repository.hash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import faang.school.urlshortenerservice.entity.hash.HashEntity;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT nextval('unique_number_seq') 
            FROM generate_series(1, :count)
        """
    )
    List<Long> getUniqueNumbers(@Param("count") int count);

    @Query(
        nativeQuery = true,
        value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                LIMIT :limit
            )
            RETURNING hash
        """
    )
    List<String> getHashBatch(@Param("limit") int limit);

}