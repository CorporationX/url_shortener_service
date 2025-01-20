package faang.school.urlshortenerservice.consumer;

import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.model.enums.UrlStatus;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.MaliciousHostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitQueueConsumerServiceTest {

    @Mock
    private MaliciousHostValidator validator;

    @Mock
    private RedisTemplate<String, String> blackListRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private RabbitQueueConsumerService rabbitQueueConsumerService;

    @Test
    void handleMessage_UnsafeUrl() {
        String urlId = "123";
        Url urlEntity = new Url();
        urlEntity.setHash(urlId);
        urlEntity.setUrl("http://evil.com");

        when(urlRepository.findById(urlId)).thenReturn(Optional.of(urlEntity));
        when(validator.isHostSafe("http://evil.com")).thenReturn(false);
        when(blackListRedisTemplate.opsForValue()).thenReturn(valueOperations);

        rabbitQueueConsumerService.handleMessage(urlId);

        verify(valueOperations).set(eq("evil.com"), eq("blacklisted"));
        verify(urlRepository).save(argThat(arg -> arg.getStatus() == UrlStatus.BLOCKED));
    }

    @Test
    void handleMessage_SafeUrl() {
        String urlId = "456";
        Url urlEntity = new Url();
        urlEntity.setHash(urlId);
        urlEntity.setUrl("http://good.com");

        when(urlRepository.findById(urlId)).thenReturn(Optional.of(urlEntity));
        when(validator.isHostSafe("http://good.com")).thenReturn(true);

        rabbitQueueConsumerService.handleMessage(urlId);

        verify(blackListRedisTemplate, never()).opsForValue();
        verify(urlRepository, never()).save(argThat(arg -> arg.getStatus() == UrlStatus.BLOCKED));
        assertEquals(UrlStatus.OK, urlEntity.getStatus());
    }

    @Test
    void handleMessage_UrlNotFound() {
        String urlId = "789";
        when(urlRepository.findById(urlId)).thenReturn(Optional.empty());

        rabbitQueueConsumerService.handleMessage(urlId);

        verify(validator, never()).isHostSafe(anyString());
        verify(blackListRedisTemplate, never()).opsForValue();
        verify(urlRepository, never()).save(any());
    }
}
