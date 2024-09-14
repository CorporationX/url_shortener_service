package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepositoryJpa extends JpaRepository<Hash, String> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :count)", nativeQuery = true)
    List<Long> getUniqueNumbers(int count);

    @Modifying
    @Query(value = "INSERT INTO hash (hash) VALUES (:hashes)", nativeQuery = true)
    List<Hash> save(List<String> hashes);

    @Query(value = """
        DELETE FROM hash
	    WHERE hash.hash IN (select hash.hash from hash limit :count)
        returning *
        """, nativeQuery = true)
    List<Hash> getHashBatch(int count);
}
