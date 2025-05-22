package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers();
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveBatch(hashes);
    }

    @PostConstruct
    public void init() {
        generateBatch();
    }
}
