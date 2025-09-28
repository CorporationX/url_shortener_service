package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = """
                    SELECT nextval('unique_number_seq')
                        FROM generate_series(1, :n)
                    """)
    List<Long> getUniqueNumbers(long n);

    void saveAll(List<Hash> hashes);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM hashes
                                WHERE hash IN (
                                    SELECT hash
                                    FROM hashes
                                    LIMIT :n
                                )
                                RETURNING *
                    """)
    List<Hash> getHashBatch(long n);
}
