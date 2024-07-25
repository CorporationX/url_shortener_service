package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCacheRepository redisRepository;
    private final UrlRepository urlRepository;
    public Url getRealUrl(String hash) {
        Optional<Url> urlFromCash = redisRepository.findById(hash);
        if (urlFromCash.isEmpty()) {
            Optional<Url> urlFromDb = urlRepository.findById(hash);
            if(urlFromDb.isPresent()) {
                redisRepository.save(urlFromDb.get());
                return urlFromDb.get();
            } else {
                throw new UrlNotFoundException("Url for hash: " + hash + " not found");
            }
        }
        return urlFromCash.get();
    }
}
