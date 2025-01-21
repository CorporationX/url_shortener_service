package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.service.cache.HashCacheImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ServiceTemplateApplicationTests {

    @MockBean
    private HashCacheImpl hashCache;

    @Test
    void contextLoads() {

    }
}
