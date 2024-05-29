package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByHash(String hash);
    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM url" +
            " WHERE create_at< DATE_SUB(CURRENT_DATE,INTERVAL 1 YEAR) RETURNING hash")
    List<Hash> deleteOldUrl();
}
