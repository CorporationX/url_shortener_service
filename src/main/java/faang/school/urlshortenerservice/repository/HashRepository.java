package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Profile(value = "jpa_profile")
@Repository
public interface HashRepository extends JpaRepository<Hash, String> { //todo: 6. Не нужно мапить эти данные на какие-либо hibernate сущности.

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :n);
            """)
    List<Long> getUniqueNumbers(long n);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash h
                   WHERE h.hash IN
                         (SELECT hash from hash LIMIT 2)
            returning h.hash;
            """)
    List<String> getHashBatch(long n);
}
