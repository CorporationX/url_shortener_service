package faang.school.urlshortenerservice.service;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.HashGenerator.Base62Encoder;
import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.chache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.liquibase.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "CREATE SEQUENCE IF NOT EXISTS unique_number_seq"
        }
)
@ActiveProfiles("it")
@Testcontainers
@Slf4j
class UrlServiceIntegrationTest {

    @Autowired
    private UrlService urlService;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private HashCache hashCache;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String shortUrl = "https://shortener/";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.6");

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.0-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "public");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS unique_number_seq");
        hashGenerator.generateBatch();
        hashCache.prepareCache();
        Thread.sleep(100);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(1000);
        urlRepository.deleteAll();
    }

    @Test
    void positiveFillingHashRepository() {
        List<Long> numbers = hashRepository.getUniqueNumbers(10);
        System.out.println("Получаю числа:" + numbers);
        List<String> hashes = base62Encoder.encode(numbers);
        System.out.println("Получаю хеши:" + hashes);
        assertEquals(10, hashes.size());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void positiveGetShortUrl() {
        String originalUrl = "https://google.com";
        System.out.println("Original URL: " + originalUrl);

        String shortLink = urlService.getShortUrlLink(originalUrl);
        System.out.println("Generated short URL: " + shortLink);

        assertNotNull(shortLink);
        assertTrue(urlRepository.count() != 0);
        System.out.println(urlRepository.count());
        assertTrue(shortLink.startsWith("http"));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void positiveGetManyShortUrl() {
        String firstOriginalLink = "https://google.com";
        String firstShortLink = urlService.getShortUrlLink(firstOriginalLink);
        System.out.println(firstShortLink);

        String secondOriginalLink = "https://google.com";
        String secondShortLink = urlService.getShortUrlLink(secondOriginalLink);
        System.out.println(secondShortLink);

        assertNotNull(firstShortLink);
        assertEquals(2, urlRepository.count());
        assertNotEquals(firstShortLink, secondShortLink);

        String firstHash = firstShortLink.replace(shortUrl, "");
        String secondHash = secondShortLink.replace(shortUrl, "");
        assertNotNull(redisTemplate.opsForValue().get(firstHash));
        assertNotNull(redisTemplate.opsForValue().get(secondHash));
    }

    @Test
    void positiveGetOriginalLink() {
        String originalLink = "https://google.com";
        String shortLink = urlService.getShortUrlLink(originalLink);
        String hash = shortLink.replace(shortUrl, "");

        Optional<Url> urlFromDb = urlRepository.findById(hash);
        assertTrue(urlFromDb.isPresent(), "URL должен быть в БД");
        assertEquals(originalLink, urlFromDb.get().getUrl());

        String cachedUrl = urlCacheRepository.findByHash(hash);
        assertNotNull(cachedUrl, "URL должен быть в кеше");
        assertEquals(originalLink, cachedUrl);

        String result = urlService.getOriginalUrl(hash);
        assertEquals(originalLink, result);
    }

}
