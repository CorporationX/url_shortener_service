package faang.school.urlshortenerservice.integrationtest;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.repository.UrlRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class UrlShortenerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UrlRepository urlRepository;

    @MockBean
    private UserContext userContext;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    private static final GenericContainer<?> redis =
            new GenericContainer<>("redis:7")
                    .withExposedPorts(6379);

    private final String testUserId = "12345";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE url RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE hash RESTART IDENTITY");
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void givenValidUrl_whenShortenUrl_thenReturnsShortUrlAndSavesToPostgresAndRedis() {
        String originalUrl = "http://localhost:8080/qwerty";
        String hash = "1";

        String shortUrl = given()
                .contentType(ContentType.JSON)
                .body(Map.of("url", originalUrl))
                .header("x-user-id", testUserId)
                .when()
                .post("/url")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        assertEquals("http://localhost:8080/" + hash, shortUrl);

        String url = given()
                .pathParam("hash", hash)
                .header("x-user-id", testUserId)
                .redirects().follow(false)
                .when()
                .get("/{hash}")
                .then()
                .statusCode(302)
                .extract()
                .header("Location");

        assertEquals(originalUrl, url);

        String cachedUrl = redisTemplate.opsForValue().get(hash);
        assertEquals(originalUrl, cachedUrl);
    }

    @Test
    void givenNonExistentHash_whenShortenUrl_thenReturns400() {
        String nonExistentHash = "xyz999";

        given()
                .pathParam("hash", nonExistentHash)
                .header("x-user-id", testUserId)
                .when()
                .get("/{hash}")
                .then()
                .statusCode(404);
    }

    @Test
    void givenInvalidUrl_whenShortenUrl_thenReturns400() {
        String invalidUrl = "";

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("url", invalidUrl))
                .header("x-user-id", testUserId)
                .when()
                .post("/url")
                .then()
                .statusCode(400);
    }

}
