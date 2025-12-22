package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueIdRepository extends JpaRepository<Hash, Long> {

    @Query(value = """
            SELECT nextval('url_sequence') from generate_series(1, :count)
            """,
            nativeQuery = true)
    List<Long> getNextRange(int count);
}
