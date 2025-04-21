package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.scheduler.CleanScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static faang.school.urlshortenerservice.IntegrationTestConstants.EXPIRED_URL_1;
import static faang.school.urlshortenerservice.IntegrationTestConstants.EXPIRED_URL_2;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class CleanSchedulerIT extends AbstractIntegrationTest {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private CleanScheduler cleanScheduler;

    @BeforeEach
    void setUp() throws Exception {
        urlRepository.saveAll(List.of(EXPIRED_URL_1, EXPIRED_URL_2));

        Field url1 = Url.class.getDeclaredField("createdAt");
        url1.setAccessible(true);
        url1.set(EXPIRED_URL_1, LocalDateTime.of(2000, 2, 10, 12, 0));

        Field url2 = Url.class.getDeclaredField("createdAt");
        url2.setAccessible(true);
        url2.set(EXPIRED_URL_2, LocalDateTime.of(2000, 3, 11, 10, 0));

        urlRepository.saveAll(List.of(EXPIRED_URL_1, EXPIRED_URL_2));
    }

    @Test
    void clean_shouldDeleteExpiredUrlsAndSaveHashes() {
        List<String> newHashes = urlRepository.findAll()
                .stream()
                .map(Url::getHash)
                .toList();

        cleanScheduler.clean();

        assertThat(newHashes)
                .isNotEmpty()
                .contains("old1", "old2");

        List<String> savedHashes = hashRepository.findAll()
                .stream().map(Hash::getHash).toList();

        assertThat(savedHashes).contains("old1", "old2");
    }
}
