package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = """
                    SELECT nextval('unique_numbers_seq')
                    FROM generate_series(1, :batchSize)
                    """)
    List<Long> getUniqueNumbers(@Param("batchSize") long batchSize);


}
