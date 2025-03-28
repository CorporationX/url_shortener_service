package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final HashRepository hashRepository;

    @Transactional
    @CachePut(value = "url", key = "#hash.hash", unless = "#result == null")
    public String saveUrlAndHash(String url, Hash hash) {
        hashRepository.delete(hash);
        return url;
    }

    @Cacheable(value = "url", key = "#hash", unless = "#result.isEmpty()")
    public Optional<String> findUrl(String hash){
        return Optional.empty();
    }
}
