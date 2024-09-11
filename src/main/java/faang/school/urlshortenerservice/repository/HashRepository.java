package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {
    @Query(value = "SELECT nextval('unique_number_seq') " +
            "FROM generate_series(1, :n)", nativeQuery = true)
    List<Long> getNumbers(@Param("n") int batchSize);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO hash (hash) VALUES (:hashes)", nativeQuery = true)
    List<String> saveAll(@Param("hashes") List<String> hashes);
}