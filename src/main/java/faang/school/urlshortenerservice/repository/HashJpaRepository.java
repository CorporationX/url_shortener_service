package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashJpaRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(Long n);


    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE hash IN (
                        SELECT * FROM hash
                                 LIMIT :n
                        )
            RETURNING *
            """)
    List<Hash> getHashBatch(Integer n);
}
