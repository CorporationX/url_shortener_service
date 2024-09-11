package faang.school.urlshortenerservice.repository;

import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<URL, Long> {
    void save(URL url);
}