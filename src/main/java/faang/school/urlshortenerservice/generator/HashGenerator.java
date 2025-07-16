package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.range:1000}")
    private int range;

    @Transactional
    @Async("hashGenExecutor")
    public void generateBatch() {
        List<Long> nextRange = hashRepository.getUniqueNumbers(range);
        List<String> hashes = encoder.encode(nextRange);
        hashRepository.save(hashes);
    }

}
