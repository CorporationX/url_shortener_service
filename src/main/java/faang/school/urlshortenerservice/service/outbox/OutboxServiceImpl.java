package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
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
        return outboxRepository.findByEventType(typeId);
    }

    @Override
    public void saveOutbox(Url url, int outboxTypeId) {
        outboxRepository.createOutbox(url.getHash(), outboxTypeId, url.getUrl());
    }

    @Override
    public void deleteOutboxBy(long id) {
        outboxRepository.deleteById(id);
    }
}
