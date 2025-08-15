package faang.school.urlshortenerservice.kafka.producer;

import faang.school.urlshortenerservice.kafka.dto.ShortUrlVisitDto;

public interface ShortUrlVisitProducer {
    void onVisit(ShortUrlVisitDto dto);
}
