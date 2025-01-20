package faang.school.urlshortenerservice;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlShortenRequest;
import faang.school.urlshortenerservice.dto.UrlShortenResponse;
import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.entiity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ServiceTemplateApplication.class})
@ActiveProfiles("test")
@Testcontainers
class UrlShortenerIntegrationTest {

    private static final String TEST_USER_ID = "1";
    private static final String TEST_HASH = "baaaaa";
    private static final String ORIGINAL_URL = "https://example.com";
    private static final int INITIAL_SEQUENCE = 916132832;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"));

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;
    private HttpHeaders headers;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("url.shortener.cache.capacity", () -> "100");
        registry.add("url.shortener.cache.refill-threshold", () -> "20");
        registry.add("url.shortener.hash.batch-size", () -> "50");
    }

    @BeforeEach
    @Transactional
    void setUp() {
        cleanupDatabase();
        initializeTestData();
        setupTestEnvironment();
    }

    private void cleanupDatabase() {
        jdbcTemplate.execute("DELETE FROM url");
        jdbcTemplate.execute("DELETE FROM hash");
        jdbcTemplate.execute("ALTER SEQUENCE url_sequence RESTART WITH " + INITIAL_SEQUENCE);
    }

    private void initializeTestData() {
        Hash hash = new Hash(TEST_HASH);
        hash.setUsed(false);
        hashRepository.save(hash);
    }

    private void setupTestEnvironment() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-USER-ID", TEST_USER_ID);
    }

    @Test
    @DisplayName("Should successfully create short URL and redirect to original URL")
    void shouldCreateAndRedirectUrl() {
        UrlShortenRequest request = new UrlShortenRequest(ORIGINAL_URL);
        HttpEntity<UrlShortenRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<UrlShortenResponse> createResponse = restTemplate.exchange(
                baseUrl + "/url",
                HttpMethod.POST,
                requestEntity,
                UrlShortenResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        String shortUrl = createResponse.getBody().getShortUrl();
        String hashValue = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        Optional<Url> savedUrl = urlRepository.findByHashValue(hashValue);
        assertThat(savedUrl)
                .isPresent()
                .hasValueSatisfying(url -> {
                    assertThat(url.getOriginalUrl()).isEqualTo(ORIGINAL_URL);
                    assertThat(url.getVisitsCount()).isEqualTo(0L);
                });

        ResponseEntity<Void> redirectResponse = restTemplate.exchange(
                baseUrl + "/" + hashValue,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(redirectResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(redirectResponse.getHeaders().getLocation())
                .isNotNull()
                .hasToString(ORIGINAL_URL);

        savedUrl = urlRepository.findByHashValue(hashValue);
        assertThat(savedUrl)
                .isPresent()
                .hasValueSatisfying(url ->
                        assertThat(url.getVisitsCount()).isEqualTo(1L)
                );

        Optional<Hash> usedHash = hashRepository.findById(savedUrl.get().getHash().getId());
        assertThat(usedHash)
                .isPresent()
                .hasValueSatisfying(hash ->
                        assertThat(hash.isUsed()).isTrue()
                );
    }

    @Test
    @DisplayName("Should return bad request when URL format is invalid")
    void shouldReturnBadRequestForInvalidUrl() {
        String invalidUrl = "not-a-url";
        UrlShortenRequest request = new UrlShortenRequest(invalidUrl);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/url",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid URL format");
    }

    @Test
    @DisplayName("Should return bad request when hash format is invalid")
    void shouldReturnBadRequestForInvalidHash() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/inv@lid",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid hash format");
    }

    @Test
    @DisplayName("Should return not found when hash does not exist")
    void shouldReturnNotFoundForNonexistentHash() {
        String validFormatHash = "abc123";
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + validFormatHash,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}