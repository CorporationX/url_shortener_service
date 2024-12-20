package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCashRepository urlCashRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    public String getOriginUrl(String hash) {
        log.info("getOriginUrl started - {}", hash);

        String url = urlCashRepository.getValue(hash)
                .orElse(urlRepository.findById(hash)
                        .orElseThrow(() -> new EntityNotFoundException("long url for: " + hash + " - does not exist"))
                        .getHash());

        log.info("getOriginUrl return - {}", url);
        return url;
    }

    @Transactional
    public UrlDto createShortUrl(UrlCreateDto urlCreateDto) {
        log.info("createShortUrl started - {}", urlCreateDto);

        Url url = new Url();
        urlRepository.findById(urlCreateDto.getUrl()).ifPresentOrElse(url1 ->
                {
                    throw new DataValidationException("This URL has already been registered.");
                }, () -> {
                    url.setUrl(urlCreateDto.getUrl().trim());
                    url.setHash(hashCache.getHash());
                    url.setCreatedAt(LocalDateTime.now());

                    urlRepository.save(url);
                    urlCashRepository.save(url.getHash(), url.getUrl());
                }
        );
        log.info("createShortUrl save to DB {}", url);
        log.info("createShortUrl save to redis - Hash:{} Url: {}", url.getHash(), url.getUrl());

        return urlMapper.toDto(url);
    }
}
