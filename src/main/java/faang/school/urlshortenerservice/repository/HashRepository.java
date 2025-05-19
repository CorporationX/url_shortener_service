package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, :count)
            """)
    List<Long> getUniqueNumbers(int count);

    @Modifying
    @Query(nativeQuery = true, value = """
                DELETE FROM hash WHERE hash IN
                (SELECT hash  FROM hash LIMIT :size FOR UPDATE SKIP LOCKED)
                RETURNING hash
            """)
    List<Hash> getHashBatch(int size);
}
