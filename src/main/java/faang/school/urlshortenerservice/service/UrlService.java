package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${url.domain}")
    private String domain;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    public UrlReadDto createShortUrl(UrlCreateDto createDto) {
        Url url = new Url();
        url.setUrl(createDto.getOriginalUrl());
        url.setHash(hashCache.getHash());

        urlCacheRepository.save(url);
        return urlMapper.toDto(url);
    }


    @Transactional
    public String getOriginalUrl(String hash) {
        Url url = urlCacheRepository.find(hash);
        log.info("Получаю ссылку по хэшу: {}", hash);
        log.info("Ccылка: {} ", url.getUrl());
        log.info("Хэш: {} ", url.getHash());
        return url.getUrl();
    }

}
