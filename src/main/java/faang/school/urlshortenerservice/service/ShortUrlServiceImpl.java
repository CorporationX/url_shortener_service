package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.property.ShortenerProperties;
import faang.school.urlshortenerservice.document.ShortUrl;
import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import faang.school.urlshortenerservice.exception.ConflictException;
import faang.school.urlshortenerservice.mapper.ShortUrlMapper;
import faang.school.urlshortenerservice.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShortUrlServiceImpl implements ShortUrlService {
    private final ShortenerProperties shortenerProperties;
    @Value("${spring.shortener.domain}")
    private String domain;
    @Value("${spring.shortener.length}")
    private int length;

    private final ShortUrlMapper mapper;
    private final ShortUrlRepository repository;
    private final UrlShortenerService shortenerService;
    private final ShortUrlCacheService cacheService;

    @Override
    public String create(CreateShortUrlDto dto) {
        ShortUrl shortUrl = mapper.toShortUrl(dto);

        String code = generateUniqueCode();
        shortUrl.setCode(code);
        repository.insert(shortUrl);
        cacheService.set(code, shortUrl.getOriginalUrl());

        return buildShortUrl(code);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < shortenerProperties.getMaxAttempts(); attempt++) {
            String code = shortenerService.generateCode(length);
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
        throw new ConflictException("Could not create unique short url after " + shortenerProperties.getMaxAttempts() + " attempts");
    }

    private String buildShortUrl(String code) {
        return String.format("%s/%s", domain, code);
    }

    @Override
    public String find(String code) {
        String url = cacheService.get(code);
        if (url == null) {
            ShortUrl mappingDocument = repository.findByCodeOrThrow(code);
            url = mappingDocument.getOriginalUrl();
            cacheService.set(mappingDocument.getCode(), url);
        }
        return url;
    }
}
