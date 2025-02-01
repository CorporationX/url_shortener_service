package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Urls;
import faang.school.urlshortenerservice.repository.interfaces.UrlsJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class UrlsRepository {
    final private UrlsJpaRepository urlsJpaRepository;

    public Urls findByHash(String hash) {
        return urlsJpaRepository.findByHash(hash).orElseThrow(() ->
                new EntityNotFoundException(String.format("Url not find by: %s", hash)));
    }
}