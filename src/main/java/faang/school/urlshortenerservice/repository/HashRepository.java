package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') AS id FROM generate_series(1, :n)")
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
                DELETE FROM hash 
                WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :batchSize) 
                RETURNING hash
            """)
    List<String> getHashBatch(@Param("batchSize") int batchSize);

}
