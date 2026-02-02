package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import faang.school.urlshortenerservice.entity.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
}
