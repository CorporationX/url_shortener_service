package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper mapper;

    /**
     * Создает короткий URL на основе переданного длинного URL.
     *
     * @param url DTO с длинным URL.
     * @return Хэш, который представляет собой короткий URL.
     */
    public String createShortUrl(LongUrlDto url) {
        log.info("Начало создания короткого URL для: {}", url.getUrl());


        Optional<Url> existingUrl = urlRepository.findByUrl(url.getUrl());
        if (existingUrl.isPresent()) {
            log.info("Короткий URL уже существует: {}", existingUrl.get().getHash());
            return existingUrl.get().getHash();
        }

        String hash = hashCache.getHash();
        Url entity = Url.builder()
            .hash(hash)
            .url(url.getUrl())
            .build();

        urlRepository.save(entity);
        urlCacheRepository.save(mapper.toRedisUrl(entity));

        log.info("Короткий URL успешно создан: {}", hash);
        return hash;
    }

    /**
     * Возвращает длинный URL по его хэшу.
     *
     * @param hash Хэш короткого URL.
     * @return Длинный URL.
     * @throws UrlNotFoundException Если URL не найден в кэше или базе данных.
     */
    public String getLongUrl(String hash) {
        log.info("Поиск длинного URL по хэшу: {}", hash);

        Optional<RedisUrl> redisUrlOptional = urlCacheRepository.findByHash(hash);
        if (redisUrlOptional.isPresent()) {
            log.info("Длинный URL найден в кэше: {}", redisUrlOptional.get().getHash());
            return redisUrlOptional.get().getHash();
        }

        Optional<Url> urlOptional = urlRepository.findUrlByHash(hash);
        if (urlOptional.isPresent()) {
            log.info("Длинный URL найден в базе данных: {}", urlOptional.get().getUrl());
            return urlOptional.get().getUrl();
        }

        log.error("URL с хэшем {} не найден", hash);
        throw new UrlNotFoundException("URL с хэшем " + hash + " не найден");
    }
}
