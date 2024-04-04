package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String>, CustomHashRepository {
    @Query(nativeQuery = true, value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *")
    List<Hash> findAndDelete(long amount);

    @Query(nativeQuery = true, value = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)")
    List<Long> getNextRange(int range);
}