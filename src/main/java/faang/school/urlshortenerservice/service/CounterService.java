package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Counter;
import faang.school.urlshortenerservice.repository.CounterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounterService {
    private final CounterRepository counterRepository;

    @Value("${app.counter-bach-size}")
    private int counterBatchSize;

    @Transactional
    public long incrementAndGet() {
        Counter counter = counterRepository.getValueForUpdate();
        counter.setValue(counter.getValue() + counterBatchSize);
        return counter.getValue();
    }
}
