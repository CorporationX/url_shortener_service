package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UrlRepository extends JpaRepository<Url,Long> {
    Set<String> findOldUrls();

    void deleteOldUrls(Set<String> hashes);
}
