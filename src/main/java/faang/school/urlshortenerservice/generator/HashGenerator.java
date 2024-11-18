package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.SaveDbJdbc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final SaveDbJdbc saveDbJdbc;

    @Value("${hash.range:1000}")
    private int range;

    @Transactional
    @Async("generateBatchExecutor")
    public void generateBatch() {
        List<String> hashes = base62Encoder.encode(hashRepository.getUniqueNumber(range));
        saveDbJdbc.save(hashes);
    }
}
