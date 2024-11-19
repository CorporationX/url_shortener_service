package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT NEXTVAL('unique_number_seq')
            FROM GENERATE_SERIES(1, :n)
            """)
    List<Long> getUniqueNumbers(long n);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN
            (
            SELECT hash.hash FROM hash
            LIMIT :n
            )
            RETURNING hash.hash
            """)
    List<Hash> getHashBatch(int n);

    @Query(nativeQuery = true, value = """
             SELECT character_maximum_length
             FROM information_schema.columns
             WHERE table_name = 'hash' AND column_name = 'hash';
            """)
    int getCharLength();
}
