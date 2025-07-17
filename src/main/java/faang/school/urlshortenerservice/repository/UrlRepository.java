package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Url findByHash(String shortHash);

    @Query(value = "" +
            "SELECT u FROM Url u " +
            "WHERE u.createdAt < CURRENT_TIMESTAMP - INTERVAL :days DAY", nativeQuery = true)
    List<Url> getUrlsOlderMoreThanDays(int days);

    @Modifying
    @Query("DELETE FROM Hash h WHERE h.hash IN :hashes")
    void deleteUrlsByHashes(List<String> hashes);
}
