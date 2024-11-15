package faang.school.urlshortenerservice.service.outbox;

import faang.school.urlshortenerservice.entity.Outbox;
import faang.school.urlshortenerservice.entity.Url;

import java.util.List;

public interface OutboxService {

    List<Outbox> getForProgressing(int typeId);

    void saveOutbox(Url url, int outboxTypeId);

    void deleteOutboxBy(long id);
}
