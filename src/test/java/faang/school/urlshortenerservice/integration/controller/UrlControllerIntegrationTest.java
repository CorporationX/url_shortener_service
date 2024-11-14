package faang.school.urlshortenerservice.integration.controller;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.integration.config.RedisPostgresTestContainersConfig;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class UrlControllerIntegrationTest extends RedisPostgresTestContainersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @PostConstruct
    void setUp() {
//        System.out.println("(-_-)");
//        sleep();
//        System.out.println("(*_*)");
    }

    @Test
    void test() {
        UrlRequestDto dto = new UrlRequestDto("http://g.com");
        urlService.createHashUrl(dto.url());
        List<Url> urls = urlRepository.findAll();
        System.out.println(urls);
    }

    void sleep() {
        try {
            System.out.println("sleep...");
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
