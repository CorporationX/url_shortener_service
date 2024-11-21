package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("""
            SELECT u.url
            FROM Url u
            WHERE u.hash = :hash
            """)
    Optional<String> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM Url u
            WHERE u.createdAt < :date
            RETURNING u.hash
            """)
    List<Hash> deleteOldUrlsAndReturnHashes(LocalDate date);
}
