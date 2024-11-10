package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.n}")
    private int n;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("asyncExecutorToHashGenerator")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        base62Encoder.encode(uniqueNumbers).thenAccept(hashes ->
                hashes.forEach(hashRepository::save)
        );
    }
}
