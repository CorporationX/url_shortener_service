package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {


    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') from generate_series(1,:count)
            """)
    List<Long> getUniqueNumbers(int count);

    @Query(nativeQuery = true, value = """
                        DELETE FROM hash h WHERE h.hash IN (SELECT * FROM hash LIMIT :count)
                        RETURNING hash
            """)
    List<String> getAndDeleteHashBatch(int count);
}