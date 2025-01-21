package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = "SELECT nextval('hash_sequence') FROM generate_series(1, :count)")
    List<Long> getUniqueSeqNumbers(Long count);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT :amount) RETURNING hash")
    List<String> getAndDeleteHashBatch(Long amount);
}