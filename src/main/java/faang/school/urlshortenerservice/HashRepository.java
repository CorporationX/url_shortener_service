package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, Long> {
    @Query(value = "select nextval(unique_number_seq) from generate_series(1, :batchSize)", nativeQuery = true)
    List<Long> getUniqueNumbers(int batchSize);

    @Query(value = "delete from hashes where hash in (select hash from hashes limit :batchSize) returning *", nativeQuery = true)
    List<Hash> getHashBatch(int batchSize);
}