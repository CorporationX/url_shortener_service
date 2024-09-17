package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = """ 
            DELETE FROM 'url'
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING *
                       """)
    List<Url> getOldHashesAndDelete();

//
    @Query(nativeQuery = true, value =
            "SELECT u.hash FROM Url u WHERE u.url = :url")
    String findHashByUrl(String url);
}