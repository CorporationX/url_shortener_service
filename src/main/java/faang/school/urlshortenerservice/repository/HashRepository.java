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
            SELECT nextval('unique_number_seq') from generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(int n);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE from hash WHERE hash in (SELECT h.* FROM hash h LIMIT :n) returning *
             """)
    List<Hash> getHashBatch(int n);
}
