package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.entity.OutboxStatus;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    public List<Outbox> getForProgressing(int typeId) {
        return outboxRepository.updateStatusByTypeAndByStatus(typeId, OutboxStatus.PENDING, OutboxStatus.IN_PROGRESS);
    }

    @Override
    public void saveOutbox(Url url) {
        outboxRepository.createOutbox(url.getHash(), OutboxCreateUrlType.OUTBOX_TYPE_ID, url.getUrl());
    }

    @Override
    public void progressToSuccess(long id) {
        outboxRepository.progressToEnd(id, OutboxStatus.SUCCESS);
    }
}
