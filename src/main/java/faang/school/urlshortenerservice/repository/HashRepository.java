package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true,
            value = """
                  SELECT hash FROM free_hash_storage
                  ORDER BY hash
                  FOR UPDATE SKIP LOCKED
                  LIMIT :batchSize""")
    List<String> getFreeHashBatch(@Param("batchSize") long batchSize);

    @Modifying
    @Query("""
            DELETE FROM Hash h
            WHERE h.hash IN :hashes""")
    void deleteAllByHashIn(@Param("hashes") List<String> hashes);
}