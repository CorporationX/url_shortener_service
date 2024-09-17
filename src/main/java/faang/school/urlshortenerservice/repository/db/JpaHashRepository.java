package faang.school.urlshortenerservice.repository.db;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaHashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :n);
            """)
    List<Long> getUniqueNumbers(long n);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash h
                   WHERE h.hash IN
                         (SELECT hash.hash from hash LIMIT :n)
            returning h.hash;
            """)
    List<String> pollHashBatch(long n);

    @Query(nativeQuery = true, value = """
            SELECT count(*) FROM hash
            """)
    int getHashesNumber();
}
