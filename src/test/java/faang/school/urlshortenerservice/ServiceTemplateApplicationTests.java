package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.HashBatchProperties;
import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.config.RedisProperties;
import faang.school.urlshortenerservice.config.ThreadPoolProperties;
import faang.school.urlshortenerservice.config.UrlServiceProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({ThreadPoolProperties.class, HashBatchProperties.class, LocalCacheProperties.class,
        RedisProperties.class, UrlServiceProperties.class})
class ServiceTemplateApplicationTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}
