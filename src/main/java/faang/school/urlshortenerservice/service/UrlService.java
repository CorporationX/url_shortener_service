package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.generator.LocalHash;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashBatchRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final LocalHash localHash;
    private final HashBatchRepository hashBatchRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${url-shortener.batch-size}")
    private int batchSize;

    @Transactional
    public void deleteOldUrl() {
        LocalDateTime dateDelete = LocalDateTime.now().minusYears(1);

        int totalCount = urlRepository.countByCreatedAtBefore(dateDelete);
        int countPages = (totalCount + batchSize - 1) / batchSize;

        IntStream.range(0, countPages).forEach(i -> deleteUrlAndSaveHash(dateDelete, i));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUrlAndSaveHash(LocalDateTime date, int pageNumber) {
        List<Url> oldUrls = urlRepository.findByCreatedAtBefore(date, PageRequest.of(pageNumber, batchSize));
        List<String> hashes = oldUrls.stream().map(Url::getHash).toList();
        urlRepository.deleteAll(oldUrls);
        hashBatchRepository.saveHashByBatch(hashes.stream()
                .map(id -> Hash.builder().hash(id).build())
                .toList());
    }

    @Transactional
    public UrlDto createShortUrl(UrlDto dto, HttpServletRequest request) {
        Url url = findByUrl(dto.url()).orElseGet(() -> {
            Hash hash = localHash.getHash();
            Url newUrl = urlMapper.toEntity(dto);
            newUrl.setHash(hash.getHash());

            urlCacheRepository.saveUrlAndHash(dto.url(), hash);
            return urlRepository.save(newUrl);
        });

        return buildShortUrl(url, request);
    }

    public ResponseEntity<Void> findOriginalUrl(String hash) {
        Optional<String> redisUrl = urlCacheRepository.findUrl(hash);

        if (redisUrl.isEmpty()) {
            Url url = urlRepository.findUrlByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("Не удалось найти оригинальную ссылку"));
            return redirect(url.getUrl());
        }
        return redirect(redisUrl.get());
    }

    private Optional<Url> findByUrl(String url) {
        return urlRepository.findUrlByUrl(url);
    }

    private UrlDto buildShortUrl(Url url, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("")
                .build()
                .toString();

        String shortUrl = String.format("%s/%s", baseUrl, url.getHash());
        return new UrlDto(shortUrl);
    }

    private ResponseEntity<Void> redirect(String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}
