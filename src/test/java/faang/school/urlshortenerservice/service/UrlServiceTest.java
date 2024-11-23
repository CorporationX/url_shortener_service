package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.base.AbstractBaseContext;
import faang.school.urlshortenerservice.dto.request.UrlRequest;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UrlServiceTest extends AbstractBaseContext {

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    @Autowired
    private UrlServiceImpl urlService;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("DELETE FROM url");
    }

    @Test
    @Transactional
    public void testDeleteUnusedUrls() {
        LocalDate date = LocalDate.now().minusDays(400);
        List<Url> urls = List.of(createUrl("hash1", "url1", date),
                createUrl("hash2", "url2", date),
                createUrl("hash3", "url3", date),
                createUrl("hash4", "url4", LocalDate.now()),
                createUrl("hash5", "url5", LocalDate.now()));
        String sql = "INSERT INTO url(hash, url, last_ttl_expiration_date) VALUES (?, ?, ?)";
        urls.forEach(url -> jdbcTemplate.update(sql, url.getHash(), url.getUrl(), url.getLastTtlExpirationDate()));

        List<String> hashes = urlService.deleteUnusedUrls();
        Set<String> hashesSet = new HashSet<>(hashes);

        assertEquals(3, hashes.size());
        assertTrue(hashesSet.contains("hash1"));
        assertTrue(hashesSet.contains("hash2"));
        assertTrue(hashesSet.contains("hash3"));
    }

    @Test
    public void testUpdateUrls() {
        List<Url> urls = List.of(createUrl("hash1", "url1", null),
                createUrl("hash2", "url2", null),
                createUrl("hash3", "url3", null),
                createUrl("hash4", "url4", null),
                createUrl("hash5", "url5", null));
        String sql = "INSERT INTO url(hash, url, last_ttl_expiration_date) VALUES (?, ?, ?)";
        urls.forEach(url -> jdbcTemplate.update(sql, url.getHash(), url.getUrl(), url.getLastTtlExpirationDate()));

        List<String> hashes = urls.stream()
                .map(Url::getHash)
                .toList();
        urlService.updateUrls(hashes);

        String selectQuery = "SELECT hash FROM url WHERE last_ttl_expiration_date::DATE = CURRENT_DATE";
        hashes = jdbcTemplate.queryForList(selectQuery, String.class);

        assertEquals(5, hashes.size());
    }

    @Test
    public void testShortenUrlWithExistsUrl() {
        UrlRequest request = new UrlRequest("url1");
        Url url = createUrl("hash1", "url1", null);
        jdbcTemplate.update(
                "INSERT INTO url(hash, url, last_ttl_expiration_date) VALUES (?, ?, ?)",
                url.getHash(), url.getUrl(), url.getLastTtlExpirationDate());

        UrlResponse urlResponse = urlService.shortenUrl(request);

        assertEquals("hash1", urlResponse.shortUrl());
    }

    @Test
    public void testShortenUrl() {
        UrlRequest request = new UrlRequest("url1");

        UrlResponse urlResponse = urlService.shortenUrl(request);

        assertNotNull(urlResponse.shortUrl());
    }

    @Test
    public void testGetUrlFromCache() {
        Url url = createUrl("hash1", "url1", null);
        urlCacheRepository.save(urlMapper.toUrlCache(url));

        String longUrl = urlService.getUrl("hash1");

        assertEquals("url1", longUrl);
    }

    @Test
    public void testGetUrlFailWithNotExistingHash() {
        assertThrows(EntityNotFoundException.class, () -> urlService.getUrl("hash10"));
    }

    @Test
    public void testGetUrlFromDataBase() {
        Url url = createUrl("hash1", "url1", null);
        jdbcTemplate.update(
                "INSERT INTO url(hash, url, last_ttl_expiration_date) VALUES (?, ?, ?)",
                url.getHash(), url.getUrl(), url.getLastTtlExpirationDate());
        Optional<UrlCache> savedUrlCache = urlCacheRepository.findById("hash1");

        assertFalse(savedUrlCache.isPresent());

        String longUrl = urlService.getUrl("hash1");
        Optional<UrlCache> urlCache = urlCacheRepository.findById("hash1");

        assertEquals("url1", longUrl);
        assertTrue(urlCache.isPresent());
    }

    private Url createUrl(String hash, String url, LocalDate date) {
        return Url.builder()
                .hash(hash)
                .url(url)
                .lastTtlExpirationDate(date)
                .build();
    }
}
