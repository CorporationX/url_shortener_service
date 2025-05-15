package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long>, HashCustomRepository {
    @Modifying
    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_seq_number')
            FROM generate_series(1, :number);
            """)
    List<String> getUniqueNumbers(@Param("number") Integer number);

    @Query("SELECT h FROM Hash h WHERE h.hash IN :hashes")
    List<Hash> findByHashes(@Param("hashes") List<String> hashes);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM Hash h WHERE h.hash IN (:hashes);
            """)
    int deleteBatch(@Param("hashes") List<String> hashes);
}
