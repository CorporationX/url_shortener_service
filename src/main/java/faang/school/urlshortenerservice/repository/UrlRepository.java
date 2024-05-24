package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = "DELETE FROM url u WHERE u.created_at < " +
            "current_timestamp - interval '1 year' RETURNING *")
    List<Url> cleanOldUrl();
}
