package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final HashCacheService hashCacheService;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    public UrlDto associateHashWithURL(UrlDto urlDto) {
        String hash = hashCacheService.getHash();

        Url entityToSave = urlMapper.toEntity(urlDto);
        entityToSave.setHash(hash);

        Url entity = save(entityToSave);
        log.info("Link and it's corresponding redirect have been successfully saved to Postgres");

        urlCacheRepository.save(entity);
        log.info("Link and it's corresponding redirect have been successfully saved to Redis");

        return urlMapper.toDto(entity);
    }

    @Transactional
    public Url save(Url url) {
        return urlRepository.save(url);
    }
}