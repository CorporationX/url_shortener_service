package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.entity.OutboxStatus;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceImplTest {

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    @Test
    void testGetForProgressing() {
        int typeId = 1;
        List<Outbox> mockOutboxList = List.of(new Outbox(), new Outbox());
        when(outboxRepository.updateStatusByTypeAndByStatus(
                typeId,
                OutboxStatus.PENDING,
                OutboxStatus.IN_PROGRESS
        )).thenReturn(mockOutboxList);

        List<Outbox> result = outboxService.getForProgressing(typeId);

        assertEquals(mockOutboxList, result);
        verify(outboxRepository).updateStatusByTypeAndByStatus(typeId, OutboxStatus.PENDING, OutboxStatus.IN_PROGRESS);
    }

    @Test
    void testSaveOutbox() {
        Url url = Url.builder()
                .hash("someHash")
                .url("https://example.com")
                .build();

        outboxService.saveOutbox(url);

        verify(outboxRepository).createOutbox("someHash", OutboxCreateUrlType.OUTBOX_TYPE_ID, "https://example.com");
    }

    @Test
    void testProgressToSuccess() {
        long id = 123L;

        outboxService.progressToSuccess(id);

        verify(outboxRepository).progressToEnd(id, OutboxStatus.SUCCESS);
    }
}