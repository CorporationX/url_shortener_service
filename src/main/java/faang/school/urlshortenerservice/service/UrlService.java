package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
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
    private final UrlMapper urlMapper;
    private final HashCash hashCash;

    public UrlDto getShortUrl(UrlDto urlDto) {
        String hash = hashCash.getHash();
        Url newUrl = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();
        Url savedUrl = urlRepository.save(newUrl);
        redisRepository.save(savedUrl);
        return urlMapper.toDto(savedUrl);
    }

    public Url getOriginUrl(String hash) {
        Optional<Url> urlFromCash = redisRepository.findById(hash);
        if (urlFromCash.isEmpty()) {
            Optional<Url> urlFromDb = urlRepository.findById(hash);
            if (urlFromDb.isPresent()) {
                redisRepository.save(urlFromDb.get());
                return urlFromDb.get();
            } else {
                throw new UrlNotFoundException("Url for hash: " + hash + " not found");
            }
        }
        return urlFromCash.get();
    }
}
