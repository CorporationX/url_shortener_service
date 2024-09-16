package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestUrlCacheRepository {
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    UrlCacheRepository urlCacheRepository;

    @Test
    public void testSaveAssociation(){
        String url = "https://www.google.com";
        String key = "url";
        urlCacheRepository.saveAssociation(url,key);
        assertThat(urlCacheRepository.getAssociation(key)).isNotNull();
    }

    @Test
    public void testGetAssociation(){
        String url = "https://www.google.com";
        String key = "url";
        urlCacheRepository.saveAssociation(url,key);
        Optional<Pair<String,String>> pair = urlCacheRepository.getAssociation(key);
        assertThat(pair.isPresent()).isTrue();
        assertThat(pair.get().getFirst()).isEqualTo(url);
        assertThat(pair.get().getSecond()).isEqualTo(key);
    }
}
