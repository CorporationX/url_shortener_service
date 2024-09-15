package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Url findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url 
            WHERE create_at< NOW() - INTERVAL '#{interval} year'
            RETURNING *
            """)
    List<Url> findAndDelete(int interval);

}
