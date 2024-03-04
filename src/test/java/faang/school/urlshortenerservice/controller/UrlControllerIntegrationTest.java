//package faang.school.urlshortenerservice.controller;
//
//import com.redis.testcontainers.RedisContainer;
//import faang.school.urlshortenerservice.ServiceTemplateApplication;
//import faang.school.urlshortenerservice.dto.UrlDto;
//import faang.school.urlshortenerservice.repository.UrlRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Testcontainers
//@SpringBootTest(classes = ServiceTemplateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class UrlControllerIntegrationTest {
//    @Autowired
//    private UrlRepository urlRepository;
//
//    @Container
//    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test")
//            .withInitScript("init-schema.sql");
//
//    @DynamicPropertySource
//    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//    }
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void testCreateShortUrl() {
//        UrlDto urlDto = new UrlDto();
//        urlDto.setUrl("http://example.com/very/long/url");
//        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/url", urlDto, String.class);
//
//        assertEquals(HttpStatus.FOUND, response.getStatusCode());
//        assertEquals("http://example.com/very/long/url", urlRepository.findAll().get(0).getUrl());
//    }
//
//}
