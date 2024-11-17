package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortLinkHashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT NEXTVAL('short_link_hash_seq') FROM generate_series(1,?)
            """)
    List<Long> getListSequences(int amount);

    @Modifying
    @Query(value = """
            DELETE FROM hash where hash IN (SELECT hash from hash ORDER BY RANDOM() LIMIT ?)
            RETURNING *
            """,
            nativeQuery = true)
    List<String> getHashBatch(int amount);
}
