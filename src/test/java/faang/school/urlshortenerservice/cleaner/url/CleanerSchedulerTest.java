package faang.school.urlshortenerservice.cleaner.url;


import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableScheduling
public class CleanerSchedulerTest extends BaseContextTest {

    @Autowired
    private CleanerScheduler cleanerScheduler;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private HashRepository hashRepository;

    private Url url1;
    private Url url2;
    private Url url3;

    @BeforeAll
    void setUp() {
        url1 = Url.builder()
                .hash("Boot")
                .url("www.google.com")
                .createdAt(LocalDateTime.of(2023, 10, 10, 10, 10))
                .build();

        url2 = Url.builder()
                .hash("Camp")
                .url("www.youtube.com")
                .createdAt(LocalDateTime.of(2023, 6, 10, 10, 10))
                .build();

        url3 = Url.builder()
                .hash("Best")
                .url("www.gmail.com")
                .createdAt(LocalDateTime.of(2024, 11, 11, 11, 11))
                .build();

        urlRepository.save(url1);
        urlRepository.save(url2);
        urlRepository.save(url3);
    }

    @Test
    public void whenThen() throws InterruptedException {
        Thread.sleep(1000);
        cleanerScheduler.clean();
        Thread.sleep(1000);
        long hashCounter = hashRepository.count();
        assertEquals(2, hashCounter);
        long urlCounter = urlRepository.count();
        assertEquals(1, urlCounter);
    }
}
