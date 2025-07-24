package faang.school.urlshortenerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import faang.school.urlshortenerservice.model.UrlJPA;

@Repository
public interface UrlRepository extends JpaRepository<UrlJPA, String> {
    @Modifying
    @Query(value = """
        DELETE FROM url 
            WHERE created_at < CURRENT_DATE - INTERVAL '1 year' 
                RETURNING hash;
        """, 
        nativeQuery = true)
    List<String> deleteAndReturnHashes();
}
