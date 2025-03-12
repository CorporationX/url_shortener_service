package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = """
            SELECT nextval('hash_id_seq')
            FROM generate_series(1, ?1);
            """,
            nativeQuery = true)
    List<Long> getUniqueNumbers(int n);
}
