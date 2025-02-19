package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.handler.ExceptionApiHandler;
import faang.school.urlshortenerservice.model.Urls;
import faang.school.urlshortenerservice.repository.RedisRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlsJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UrlShortenerControllerIT {
    @Autowired
    private UrlShortenerController urlShortenerController;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private UrlsJpaRepository urlsJpaRepository;

    private MockMvc mockMvc;

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerControllerIT.class);

    @Value("${hash.test-url.url-name}")
    private String expectedUrl;

    private static String hashTest;
    private static String shortUrlTest;
    private String longUrlTest;
    private String urlController;

    @BeforeEach
    void setUp() {
        urlController = "/api/url_shortener/v1/url";
        longUrlTest = "http://www.test-urlshortener.com/long-url/v1/there-is-a-long-url-here";
        mockMvc = MockMvcBuilders
                .standaloneSetup(urlShortenerController)
                .setControllerAdvice(new ExceptionApiHandler())
                .build();
    }

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void getShortUrlSuccessTest() throws Exception {
        log.info("====================== Start: getShortUrlSuccessTest()");
        UrlDto longUrlDto = new UrlDto(longUrlTest);

        MvcResult urlDtoResult = mockMvc.perform(post(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(longUrlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        log.info(urlDtoResult.getResponse().getContentAsString());

        UrlDto urlDto = new ObjectMapper().readValue(urlDtoResult.getResponse().getContentAsString(), UrlDto.class);

        assertTrue(urlDto.url().contains(expectedUrl), "The response does not contain the expected url");

        shortUrlTest = urlDto.url();
        log.info("shortUrl: " + shortUrlTest);

        hashTest = shortUrlTest.replaceAll(".*/([^/]+)$", "$1");
        log.info("Hash: " + hashTest);

        String urlRedisResult = redisRepository.getUrl(hashTest);
        log.info("Url from Redis: " + urlRedisResult);

        Urls urlsResult = urlsJpaRepository.findByHash(hashTest).orElseThrow(() -> null);
        log.info("Url from DB: " + urlsResult.getUrl());

        assertEquals(longUrlTest, urlRedisResult, "The url from Redis is not as expected");
        assertEquals(longUrlTest, urlsResult.getUrl(), "The url from DB is not as expected");
        log.info("====================== End: getShortUrlSuccessTest() ");
    }

    @Test
    @Order(2)
    void getLongUrlSuccessTest() throws Exception {
        log.info("====================== Start: getLongUrlSuccessTest()");
        log.info("shortUrlTest : {}", shortUrlTest);

        UrlDto shortUrlDto = new UrlDto(shortUrlTest);

        MvcResult urlDtoResult = mockMvc.perform(get(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(shortUrlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        log.info(urlDtoResult.getResponse().getContentAsString());

        UrlDto urlDto = new ObjectMapper().readValue(urlDtoResult.getResponse().getContentAsString(), UrlDto.class);

        assertTrue(urlDto.url().contains(longUrlTest), "The response does not contain the expected url");
        log.info("====================== End: getLongUrlSuccessTest()");
    }

    @Test
    @Order(3)
    void getLongUrlByHashSuccessTest() throws Exception {
        log.info("====================== Start: getLongUrlByHashSuccessTest()");

        MvcResult mvcResult = mockMvc.perform(get(urlController + "/" + hashTest))
                .andDo(print())
                .andExpect(status().isFound())
                .andReturn();

        log.info("Url header Location: {}", mvcResult.getResponse().getHeader("Location"));
        assertTrue(mvcResult.getResponse().getHeader("Location").contains(longUrlTest), "The response does not contain the expected url");
        log.info("====================== End: getLongUrlByHashSuccessTest()");
    }

    @Test
    @Order(4)
    void getLongUrlByHashNotFindHashFailTest() throws Exception {
        log.info("====================== Start: getLongUrlByHashNotFindHashFailTest()");
        String errorHashExtension = hashTest + "&_";

        mockMvc.perform(get(urlController + "/" + errorHashExtension))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Url not find by " + errorHashExtension))
                .andReturn();

        log.info("====================== End: getLongUrlByHashNotFindHashFailTest()");
    }

    @Test
    @Order(4)
    void getLongUrlByHashWrongHashLengthFailTest() throws Exception {
        log.info("====================== Start: getLongUrlByHashNotFindHashFailTest()");
        String errorMessage = "Url has incorrect hash ";
        String errorHashTest = "12_test_length_hash";


        mockMvc.perform(get(urlController + "/" + errorHashTest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage + errorHashTest));

        log.info("====================== End: getLongUrlByHashNotFindHashFailTest()");
    }

    @Test
    @Order(4)
    void getShortUrlIsValidLongUrlFailTest() throws Exception {
        log.info("====================== Start: getShortUrlIsValidLongUrlFailTest()");

        String errorMessage = "Incorrect url ";
        String wrongLongUrl = "http://www.test-urlshortener.com\\long-url/v1/there-is-a-long-url-here";
        UrlDto longUrlDto = new UrlDto(wrongLongUrl);

        mockMvc.perform(post(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(longUrlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage + wrongLongUrl));

        log.info("====================== End: getShortUrlIsValidLongUrlFailTest()");
    }

    @Test
    @Order(4)
    void isValidShortUrlIsValidLongUrlFailTest() throws Exception {
        log.info("====================== Start: getLongUrlIsValidLongUrlFailTest()");

        String errorMessage = "Incorrect url ";
        String wrongLongUrl = "http://www.test-urlshortener.com\\22";
        UrlDto shortUrlDto = new UrlDto(wrongLongUrl);

        mockMvc.perform(get(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(shortUrlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage + wrongLongUrl));

        log.info("====================== End: getLongUrlIsValidLongUrlFailTest()");
    }
}