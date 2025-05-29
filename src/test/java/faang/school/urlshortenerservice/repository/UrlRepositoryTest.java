package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContainersConfiguration.class)
@DisplayName("UrlRepository Test")
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    @Transactional
    @DisplayName("Save and find URL by hash")
    void saveAndFindByHash() {
        String hash = "abc123";
        String originalUrl = "https://www.example.com/test1";
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);

        Optional<Url> foundUrl = urlRepository.findByHash(hash);
        assertTrue(foundUrl.isPresent());
        assertEquals(originalUrl, foundUrl.get().getUrl());
    }

    @Test
    @Transactional
    @DisplayName("Find hash by URL")
    void findHashByUrl() {
        String hash = "def456";
        String originalUrl = "https://www.example.com/test2";
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);

        String foundHash = urlRepository.findHashByUrl(originalUrl);
        assertEquals(hash, foundHash);
    }

    @Test
    @Transactional
    @DisplayName("Delete expired URLs")
    void deleteExpiredUrls() {
        String hash = "ghi789";
        String originalUrl = "https://www.example.com/test3";
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now().minusYears(2))
                .build();

        urlRepository.save(url);

        List<String> deletedHashes = urlRepository.deleteExpiredUrls();
        assertEquals(1, deletedHashes.size());
        assertEquals(hash, deletedHashes.get(0));

        Optional<Url> foundUrl = urlRepository.findByHash(hash);
        assertFalse(foundUrl.isPresent());
    }
}
