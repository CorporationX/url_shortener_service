package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Modifying
    @Query(value = "INSERT INTO hash (hash) VALUES (:hashes)", nativeQuery = true)
    void saveHashes(@Param("hashes") List<String> hashes);

    @Modifying
    @Query(value = "DELETE FROM Hash WHERE hash IN (SELECT hash FROM Hash LIMIT :n)" +
            " RETURNING hash", nativeQuery = true)
    List<String> getHashBatch(@Param("n") int n);
}
