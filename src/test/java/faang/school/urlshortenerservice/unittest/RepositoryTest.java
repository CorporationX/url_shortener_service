package faang.school.urlshortenerservice.unittest;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@ComponentScan(basePackages = "faang.school.urlshortenerservice.config")
class RepositoryTest {

    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private org.springframework.data.redis.cache.RedisCacheManager redisCacheManager;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void testSaveAndRetrieveHashAndUrl() {

        Url url = Url.builder()
                .hash("abc123")
                .url("https://example.com")
                .build();


        Hash hash = Hash.builder()
                .hash("abc123")
                .url(url)
                .build();

        url.setHashEntity(hash);


        hashRepository.save(hash);

        // Retrieve and Assert
        Optional<Hash> retrievedHash = hashRepository.findById("abc123");
        assertTrue(retrievedHash.isPresent());
        assertEquals("https://example.com", retrievedHash.get().getUrl().getUrl());

        Optional<Url> retrievedUrl = urlRepository.findById("abc123");
        assertTrue(retrievedUrl.isPresent());
        assertEquals("abc123", retrievedUrl.get().getHash());
    }
}
