package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_hash_number_seq') AS unique_number FROM generate_series(1, :numbers)
            """)
    List<Long> getUniqueNumbers(@Param("numbers") int numbers);

    @Modifying
    @Query(nativeQuery = true, value = """
                        DELETE FROM hash
                                WHERE hash IN (
                                    SELECT hash
                                    FROM hash
                                    ORDER BY RANDOM()
                                    LIMIT :numbers
                                    FOR UPDATE SKIP LOCKED
                                )
                                RETURNING hash;
            """)
    List<String> getHashBatch(@Param("numbers") int numbers);

    @Query(nativeQuery = true, value = """
                    SELECT minimum_value
                    FROM information_schema.sequences
                    WHERE sequence_name = 'unique_hash_number_seq';
            """)
    long getSequenceMin();

    @Query(nativeQuery = true, value = """
                    SELECT maximum_value
                    FROM information_schema.sequences
                    WHERE sequence_name = 'unique_hash_number_seq';
            """)
    long getSequenceMax();

}
