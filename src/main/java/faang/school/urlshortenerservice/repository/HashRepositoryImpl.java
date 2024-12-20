package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepositoryImpl extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
               SELECT nextval('unique_number_seq') FROM generate_series(1, :maxRange)
            """)
    List<Long> getUniqueNumbers(long maxRange);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM hashes WHERE hash IN (SELECT hash FROM hashes LIMIT ?) RETURNING *
            """)
    List<Hash> findAndDelete(long amount);
}