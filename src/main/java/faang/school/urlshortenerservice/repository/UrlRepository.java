package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM url WHERE created_at < now() - interval '1 year' RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes();
}

