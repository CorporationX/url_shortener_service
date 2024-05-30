package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        log.info("Создания short url для {}", urlDto.getUrl());
        String  hash = hashCache.getHash();
        log.info("Получили хэш из кэша {}", hash);
        urlCacheRepository.putUrl(hash, urlDto.getUrl());
        log.info("Сохранили url и хэш в urlCacheRepository");
        log.info("Начинаем формировать сущность Url");
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(urlDto.getUrl());
        Url saveUrl = urlRepository.save(url);
        log.info("Сущность {} успешно сохранена в БД", saveUrl);
        return urlMapper.toDto(saveUrl);
    }

    public RedirectView getRedirectView(String hash) {
        log.info("Старт поиска Url адреса на хэшу");
        String url = urlCacheRepository.getUrl(hash)
                .orElseGet(() -> urlRepository.findById(hash)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", hash)))
                        .getUrl());
        log.info("Url {} получен и переноправил юзера" ,url);
        return new RedirectView(url);
    }

}
