package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            DELETE FROM urls WHERE created_at < DATE_SUB(CURRENT_DATE,INTERVAL 1 YEAR)
            RETURNING hash
            """)
    @Modifying
    @Transactional
    List<String> deleteUrlsAndSaveHashes();
}
