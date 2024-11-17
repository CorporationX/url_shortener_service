package faang.school.urlshortenerservice.integration.scheduler;

import faang.school.urlshortenerservice.integration.IntegrationTestBase;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.scheduler.CleanerScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class CleanerSchedulerTest extends IntegrationTestBase {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CleanerScheduler cleanerScheduler;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashRepository hashRepository;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("urls")).put("3fvsRf", "https://testurl.com/test/2");
        Objects.requireNonNull(cacheManager.getCache("urls")).put("3fvsdR", "https://testurl.com/test/4");
        Objects.requireNonNull(cacheManager.getCache("urls")).put("4fvscR", "https://testurl.com/test/5");
    }

    @Test
    @DisplayName("Remove old urls removes URLS successfully from Database and cache")
    void testRemoveOldUrlsSuccess() throws InterruptedException {
        cleanerScheduler.removeOldUrls();
        Thread.sleep(1000); // даю закончить работу асинхронности
        List<Hash> hashes = hashRepository.findAll();
        List<Url> urls = urlRepository.findAll();
        String cache1 = Objects.requireNonNull(cacheManager.getCache("urls")).get("3fvsRf", String.class);
        String cache2 = Objects.requireNonNull(cacheManager.getCache("urls")).get("3fvsdR", String.class);
        String cache3 = Objects.requireNonNull(cacheManager.getCache("urls")).get("4fvscR", String.class);

        assertThat(urls.size()).isEqualTo(1);
        assertThat(urls).doesNotContainAnyElementsOf(List.of(
                new Url("3fvsRf", "https://testurl.com/test/2", LocalDateTime.parse("2023-11-01T11:00:00")),
                new Url("3fvsdR", "https://testurl.com/test/4", LocalDateTime.parse("2023-09-01T11:00:00")),
                new Url("4fvscR", "https://testurl.com/test/5", LocalDateTime.parse("2023-08-01T11:00:00"))
        ));
        assertThat(hashes).hasSize(3);
        assertThat(cache1).isNull();
        assertThat(cache2).isNull();
        assertThat(cache3).isNull();
    }
}