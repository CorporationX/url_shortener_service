package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO hash (hash_value, is_used) 
        SELECT unnest(CAST(:hashes AS text[])), false
        """, nativeQuery = true)
    void saveReleasedHashes(@Param("hashes") List<String> hashes);
}