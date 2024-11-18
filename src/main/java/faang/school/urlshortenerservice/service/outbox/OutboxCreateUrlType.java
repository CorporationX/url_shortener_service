package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.service.cache.CacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;

@Component
public class OutboxCreateUrlType extends OutboxType {

    private final CacheService<String> cacheService;
    private final TransactionTemplate transactionTemplate;

    public static final int OUTBOX_TYPE_ID = 0;

    @Value("${server.cache.ttl-initial-time-sec}")
    private long initialTtlSeconds;

    public OutboxCreateUrlType(OutboxService outboxService,
                               CacheService<String> cacheService,
                               TransactionTemplate transactionTemplate) {
        super(outboxService);
        this.cacheService = cacheService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    @Scheduled(fixedRateString = "${server.outbox.scheduled.create-url-time-ms}")
    public void progressOutbox() {
        List<Outbox> outboxesForProgressing = outboxService.getForProgressing(getId());
        outboxesForProgressing.forEach(outbox -> transactionTemplate.execute(status -> {
            cacheService.put(
                    outbox.getEntityId(),
                    outbox.getPayload(),
                    Duration.ofSeconds(initialTtlSeconds)
            );
            outboxService.deleteOutboxBy(outbox.getId());
            return null;
        }));
    }

    @Override
    public int getId() {
        return OUTBOX_TYPE_ID;
    }
}
