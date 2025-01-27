package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlAssociation, String> {

    boolean existsByUrl(String url);

    UrlAssociation findByUrl(String url);


    @Modifying
    @Query(nativeQuery = true, value = """
                   DELETE FROM url
                   WHERE created_at < NOW() - INTERVAL '1 year' 
                   RETURNING hash
                   """)
    List<String> deleteOldUrls();
}
