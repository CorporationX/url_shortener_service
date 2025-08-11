package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {

    @Query(value = "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getUniqueNumbers(@Param("n") int n);

    @Query(value = "SELECT hash FROM hash ORDER BY random() LIMIT :n", nativeQuery = true)
    List<String> findRandomHashes(@Param("n") int n);

    @Modifying
    @Query(value = "DELETE FROM hash WHERE hash IN :ids", nativeQuery = true)
    void deleteAllByIdInBatch(@Param("ids") List<String> ids);

    @Query(value = "INSERT INTO hash (value) VALUES (:hash)", nativeQuery = true)
    void saveHash(@Param("hash") String hash);

    default void saveAll(List<String> hashes) {
        hashes.forEach(this::saveHash);
    }
}