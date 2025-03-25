package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String>, HashCustomRepository {

    @Query(value = """
            SELECT nextval('hash_id_seq')
            FROM generate_series(1, ?1);
            """,
            nativeQuery = true)
    List<Long> getUniqueNumbers(Long n);

    @Modifying
    @Query(value = """
            DELETE FROM hash
            WHERE ctid IN (
                SELECT ctid
                FROM hash
                LIMIT ?1
            )
            RETURNING *
    """, nativeQuery = true)
    List<Hash> getHashBatchAndDelete(int size);
}
