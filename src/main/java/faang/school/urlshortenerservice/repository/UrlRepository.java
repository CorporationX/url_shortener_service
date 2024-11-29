package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(value = "SELECT * FROM url WHERE hash = ?1", nativeQuery = true)
    Url findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM url u WHERE u.created_at < NOW() - INTERVAL '1 year' RETURNING u.hash")
    List<Hash> deleteOneYearUrl();
}
