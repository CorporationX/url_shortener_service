package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.service.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxCreateUrlTypeTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private CacheService<String> cacheService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private OutboxCreateUrlType outboxCreateUrlType;

    private long initialTtlSeconds;

    @BeforeEach
    void setUp() {
        initialTtlSeconds = 2L;
        ReflectionTestUtils.setField(outboxCreateUrlType, "initialTtlSeconds", initialTtlSeconds);
    }

    @Test
    void testProgressOutbox() {
        String entityId = "entityId";
        String payload = "payload";
        Outbox outbox = Outbox.builder()
                .id(1L)
                .entityId(entityId)
                .payload(payload)
                .build();

        when(outboxService.getForProgressing(outboxCreateUrlType.getId())).thenReturn(List.of(outbox));
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            invocation.getArgument(0, TransactionCallback.class).doInTransaction(null);
            return null;
        });

        outboxCreateUrlType.progressOutbox();

        verify(outboxService).getForProgressing(outboxCreateUrlType.getId());
        verify(cacheService).put(entityId, payload, Duration.ofSeconds(initialTtlSeconds));
        verify(outboxService).progressToSuccess(outbox.getId());
    }

    @Test
    void testGetId() {
        assertEquals(0, outboxCreateUrlType.getId());
    }
}