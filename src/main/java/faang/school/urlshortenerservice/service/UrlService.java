package faang.school.urlshortenerservice.service;

import java.net.URI;
import java.util.Optional;

import org.springframework.stereotype.Service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlJPAMapper;
import faang.school.urlshortenerservice.mapper.UrlRedisMapper;
import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.UrlJPA;
import faang.school.urlshortenerservice.model.UrlRedis;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlRedisRepository urlRedisRepository;
    private final UrlJPAMapper urlJPAMapper;
    private final UrlRedisMapper urlRedisMapper;

    public UrlDto generateUrl(URI uri) {
        String hash = hashCache.getHash();
        UrlJPA urlJPA = new UrlJPA();
        urlJPA.setHash(hash);
        urlJPA.setUrl(uri.toString());

        UrlRedis urlRedis = new UrlRedis();
        urlRedis.setHash(hash);
        urlRedis.setUrl(uri.toString());

        urlRedisRepository.save(urlRedis);
        UrlJPA savedUrlJpa = urlRepository.save(urlJPA);

        return urlJPAMapper.toDto(savedUrlJpa);
    }

    public UrlDto getUrlByHash(String hash) {
        UrlDto urlDto = new UrlDto();
        Optional<UrlRedis> urlRedis = urlRedisRepository.findById(hash);
        if (urlRedis.isPresent()) {
            urlDto = urlRedisMapper.toDto(urlRedis.get());
        } else {
            Optional<UrlJPA> urlJPA = urlRepository.findById(hash);
            if (urlJPA.isPresent()) {
                urlDto = urlJPAMapper.toDto(urlJPA.get());
            }
        }

        if (urlDto.getUrl() == null)
            throw new EntityNotFoundException("Invalid hash.");

        return urlDto;
    }
}
