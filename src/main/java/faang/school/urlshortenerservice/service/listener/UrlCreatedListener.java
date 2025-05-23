package faang.school.urlshortenerservice.service.listener;

import faang.school.urlshortenerservice.dto.UrlCreatedEvent;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UrlCreatedListener {
    private final UrlCacheRepository urlCacheRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUrlCreatedEvent(UrlCreatedEvent event) {
        urlCacheRepository.save(event.hash(), event.originalUrl());
    }
}