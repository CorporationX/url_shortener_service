package faang.school.urlshortenerservice.service.outbox;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OutboxType {

    protected final OutboxService outboxService;

    public abstract void progressOutbox();

    public abstract int getId();
}
