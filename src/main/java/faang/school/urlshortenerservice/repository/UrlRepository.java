package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import faang.school.urlshortenerservice.model.Url;

public interface UrlRepository extends JpaRepository<Url, String> {

}
